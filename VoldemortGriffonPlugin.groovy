/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Andres Almiray
 */
class VoldemortGriffonPlugin {
    // the plugin version
    String version = '0.3'
    // the version or versions of Griffon the plugin is designed for
    String griffonVersion = '1.1.0 > *'
    // the other plugins this plugin depends on
    Map dependsOn = [:]
    // resources that are included in plugin packaging
    List pluginIncludes = []
    // the plugin license
    String license = 'Apache Software License 2.0'
    // Toolkit compatibility. No value means compatible with all
    // Valid values are: swing, javafx, swt, pivot, gtk
    List toolkits = []
    // Platform compatibility. No value means compatible with all
    // Valid values are:
    // linux, linux64, windows, windows64, macosx, macosx64, solaris
    List platforms = []
    // URL where documentation can be found
    String documentation = ''
    // URL where source can be found
    String source = 'https://github.com/griffon/griffon-voldemort-plugin'

    List authors = [
        [
            name: 'Andres Almiray',
            email: 'aalmiray@yahoo.com'
        ]
    ]
    String title = 'Voldemort support'
    String description = '''
The Voldemort plugin enables lightweight access to [Voldemort][1] databases.
This plugin does NOT provide domain classes nor dynamic finders like GORM does.

Usage
-----
Upon installation the plugin will generate the following artifacts in `$appdir/griffon-app/conf`:

 * VoldemortConfig.groovy - contains the database definitions.
 * BootstrapVoldemort.groovy - defines init/destroy hooks for data to be manipulated during app startup/shutdown.

Two dynamic methods named `withVoldemort` and `withVoldemortStore` will be injected into all controllers,
giving you access to a `voldemort.client.StoreClientFactory` object, with which you'll be able
to make calls to the database. Remember to make all database calls off the EDT
otherwise your application may appear unresponsive when doing long computations
inside the EDT.

These methods are aware of multiple databases. If no databaseName is specified when calling
it then the default database will be selected. Here are two example usages, the first
queries against the default database while the second queries a database whose name has
been configured as 'internal'

    package sample
    class SampleController {
        def queryAllDatabases = {
            withVoldemort { clientName, clientFactory -> ... }
            withVoldemort('internal') { clientName, clientFactory -> ... }
        }
    }

These methods are also accessible to any component through the singleton `griffon.plugins.voldemort.VoldemortConnector`.
You can inject these methods to non-artifacts via metaclasses. Simply grab hold of a particular metaclass and call
`VoldemortEnhancer.enhance(metaClassInstance, voldemortProviderInstance)`.

Configuration
-------------
### Dynamic method injection

Dynamic methods will be added to controllers by default. You can
change this setting by adding a configuration flag in `griffon-app/conf/Config.groovy`

    griffon.voldemort.injectInto = ['controller', 'service']

### Events

The following events will be triggered by this addon

 * VoldemortConnectStart[config, databaseName] - triggered before connecting to the database
 * VoldemortConnectEnd[databaseName, database] - triggered after connecting to the database
 * VoldemortDisconnectStart[config, databaseName, database] - triggered before disconnecting from the database
 * VoldemortDisconnectEnd[config, databaseName] - triggered after disconnecting from the database

### Multiple Stores

The config file `VoldemortConfig.groovy` defines a default database block. As the name
implies this is the database used by default, however you can configure named databases
by adding a new config block. For example connecting to a database whose name is 'internal'
can be done in this way

    clients {
        internal {
            config {
                bootstrapUrls = ['tcp://localhost:6666']
            }
        }
    }

This block can be used inside the `environments()` block in the same way as the
default client block is used.

### Example

A trivial sample application can be found at [https://github.com/aalmiray/griffon_sample_apps/tree/master/persistence/voldemort][2]

Testing
-------
Dynamic methods will not be automatically injected during unit testing, because addons are simply not initialized
for this kind of tests. However you can use `VoldemortEnhancer.enhance(metaClassInstance, voldemortProviderInstance)` where 
`voldemortProviderInstance` is of type `griffon.plugins.voldemort.VoldemortProvider`. The contract for this interface looks like this

    public interface VoldemortProvider {
        Object withVoldemort(Closure closure);
        Object withVoldemort(String clientName, Closure closure);
        <T> T withVoldemort(CallableWithArgs<T> callable);
        <T> T withVoldemort(String clientName, CallableWithArgs<T> callable);
        Object withVoldemortStore(String storeName, Closure closure);
        Object withVoldemortStore(String clientName, String storeName, Closure closure);
        <T> T withVoldemortStore(String storeName, CallableWithArgs<T> callable);
        <T> T withVoldemortStore(String clientName, String storeName, CallableWithArgs<T> callable);
    }

It's up to you define how these methods need to be implemented for your tests. For example, here's an implementation that never
fails regardless of the arguments it receives

    class MyVoldemortProvider implements VoldemortProvider {
        Object withVoldemort(String clientName = 'default', Closure closure) { null }
        public <T> T withVoldemort(String clientName = 'default', CallableWithArgs<T> callable) { null }
        Object withVoldemortStore(String clientName = 'default', String storeName, Closure closure) { null }
        public <T> T withVoldemortStore(String clientName = 'default', String storeName, CallableWithArgs<T> callable) { null }
    }

This implementation may be used in the following way

    class MyServiceTests extends GriffonUnitTestCase {
        void testSmokeAndMirrors() {
            MyService service = new MyService()
            VoldemortEnhancer.enhance(service.metaClass, new MyVoldemortProvider())
            // exercise service methods
        }
    }


[1]: http://project-voldemort.com
[2]: https://github.com/aalmiray/griffon_sample_apps/tree/master/persistence/voldemort
'''
}
