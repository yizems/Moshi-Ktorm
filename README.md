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

### LICENSE

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
