springboot-kafka
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           └── demo
│   │   │               ├── SpringbootKafkaApplication.java
│   │   │               ├── config
│   │   │               │   ├── CommerceToolsClientConfig.java
│   │   │               │   ├── CommerceToolsImportConfig.java
│   │   │               │   └── KafkaConnectConfig.java
│   │   │               ├── controller
│   │   │               │   ├── OrderController.java
│   │   │               │   └── ProductController.java
│   │   │               ├── dto
│   │   │               │   ├── OrderRequest.java
│   │   │               │   └── ProductRequest.java
│   │   │               ├── kafka
│   │   │               │   ├── consumer
│   │   │               │   │   ├── OrderConsumer.java
│   │   │               │   │   └── ProductConsumer.java
│   │   │               │   ├── producer
│   │   │               │   │   ├── OrderProducer.java
│   │   │               │   │   └── ProductProducer.java
│   │   │               │   └── service
│   │   │               │       ├── ProductImportService.java
│   │   │               │       └── ProductSyncService.java
│   │   │               └── connect
│   │   │                   ├── MySqlSourceConnector.java
│   │   │                   └── MySqlSourceTask.java
│   │   └── resources
│   │       ├── application.yml
│   │       └── connect-standalone.properties
│   └── test
│       └── java
│           └── com
│               └── example
│                   └── demo
│                       └── SpringbootKafkaApplicationTests.java
├── pom.xml
└── README.md