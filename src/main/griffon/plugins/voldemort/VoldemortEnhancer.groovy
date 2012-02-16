/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.plugins.voldemort

import griffon.util.CallableWithArgs

/**
 * @author Andres Almiray
 */
final class VoldemortEnhancer {
    private VoldemortEnhancer() {}
    
    static void enhance(MetaClass mc, VoldemortProvider provider = StoreClientHolder.instance) {
        mc.withVoldemort = {Closure closure ->
            provider.withVoldemort('default', closure)
        }
        mc.withVoldemort << {String clientName, Closure closure ->
            provider.withVoldemort(clientName, closure)
        }
        mc.withVoldemort << {CallableWithArgs callable ->
            provider.withVoldemort('default', callable)
        }
        mc.withVoldemort << {String clientName, CallableWithArgs callable ->
            provider.withVoldemort(clientName, callable)
        }
        mc.withVoldemortStore = {String clientName, Closure closure ->
            provider.withVoldemortStore('default', storeName, closure)
        }
        mc.withVoldemortStore << {String clientName, String storeName, Closure closure ->
            provider.withVoldemortStore(clientName, storeName, closure)
        }
        mc.withVoldemortStore << {String clientName, CallableWithArgs callable ->
            provider.withVoldemortStore('default', storeName, callable)
        }
        mc.withVoldemortStore << {String clientName, String storeName, CallableWithArgs callable ->
            provider.withVoldemortStore(clientName, storeName, callable)
        }
    }
}
