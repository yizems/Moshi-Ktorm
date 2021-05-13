## Moshi 对 Ktorm Entity 的兼容

### 说明

[![](https://jitpack.io/v/yizems/Moshi-Ktorm.svg)](https://jitpack.io/#yizems/Moshi-Ktorm)

`-fork`后缀的版本,是为 https://github.com/yizems/moshi 创建的,具体请看该库的特性

```
implementation 'com.github.yizems:Moshi-Ktorm:0.0.1-fork'
```

不带`-fork` 后缀的版本 是使用的官方库
```
implementation 'com.github.yizems:Moshi-Ktorm:0.0.1'
```

### 使用方式

```
Moshi.Builder()
.addKtormEntityJsonFactory()
.build()
```

