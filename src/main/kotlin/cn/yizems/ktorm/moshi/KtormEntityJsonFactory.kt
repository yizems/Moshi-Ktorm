package cn.yizems.ktorm.moshi

import com.squareup.moshi.*
import com.squareup.moshi.Moshi.Builder
import me.liuwj.ktorm.entity.Entity
import java.lang.reflect.Type
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaGetter

/**
 * 添加 Moshi 适配 Ktorm 实体的 JsonAdapterFactory
 */
fun Builder.addKtormEntityJsonFactory(): Builder {
    add(KtormEntityJsonFactory())
    return this
}

/**
 * 适配 Ktorm 实体的 JsonAdapterFactory
 */
class KtormEntityJsonFactory : JsonAdapter.Factory {

    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        val rawType = Types.getRawType(type)
        if (Entity::class.java.isAssignableFrom(rawType)) {
            return KtormEntityJsonAdapter(rawType, moshi)
        }
        return null
    }
}

/**
 * `Moshi` 会缓存这个类,所以成员变量的保留还是有必要的
 */
class KtormEntityJsonAdapter(type: Class<*>, private val moshi: Moshi) : JsonAdapter<Entity<*>>() {

    private val kType = type.kotlin

    private val properties
        get() = kType.memberProperties
            .filterNot {
                it.name == "properties"
                        || it.name == "entityClass"
            }
            .filter {
                it.findAnnotation<Transient>() == null
            }

    private val propertyConfigs by lazy {
        properties.map {
            val noTransientAnnotation = it.findAnnotation<Transient>() == null
            PropertyConfig(
                it.name,
                it.javaGetter!!.returnType,
                it.findAnnotation<Json>()?.name,
                noTransientAnnotation,
                noTransientAnnotation,
            )
        }
    }

    override fun fromJson(reader: JsonReader): Entity<*> {
        val entity = Entity.create(kType)


        val dpc = propertyConfigs.filter { it.deserialize }
            .toMutableList()

        if (dpc.isEmpty()) {
            return entity
        }

        reader.beginObject()

        while (reader.hasNext()) {
            val name = reader.nextName()
            val property = dpc.firstOrNull { it.hit(name) }

            if (property == null) {
                reader.skipValue()
                continue
            }
            dpc.remove(property)

            val value = moshi.adapter(property.jType)
                .fromJson(reader)

            entity[property.name] = value
        }

        reader.endObject()

        return entity
    }

    override fun toJson(writer: JsonWriter, value: Entity<*>?) {
        value ?: return

        val spc = propertyConfigs.filter { it.serialize }

        if (spc.isEmpty()) {
            return
        }

        writer.beginObject()

        spc.forEach { config ->
            val v = value.properties[config.name]
            if (v != null) {
                writer.name(config.getSerializeName())
                moshi.adapter(v.javaClass).toJson(writer, v)
            }
        }

        writer.endObject()
    }


    private data class PropertyConfig<T>(
        val name: String,
        val jType: Class<T>,
        val jsonName: String? = null,
        // 这两个字段主要是用于以后扩展 某个属性仅支持 序列化/反序列化
        val serialize: Boolean = true,
        val deserialize: Boolean = true,
    ) {
        /** 反序列化时 名字匹配 */
        fun hit(rName: String?): Boolean {
            rName ?: return false
            if (jsonName != null) {
                return jsonName.equals(rName, true)
            }
            return name.equals(rName, true)
        }

        fun getSerializeName() = jsonName ?: name

        override fun hashCode(): Int {
            return name.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            other ?: return false
            if (other !is PropertyConfig<*>) {
                return false
            }
            return this.name == other.name
        }
    }
}
