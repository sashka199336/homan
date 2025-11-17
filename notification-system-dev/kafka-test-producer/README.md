# Модуль Kafka Test Producer  

## Обзор  
kafka-test-producer — это Spring Boot приложение для генерации и отправки тестовых уведомлений в Kafka.  
Основные функции:  
* Автоматическая отправка примеров уведомлений при старте;  
* Отправка кастомных уведомлений через REST API;  
* Тестирование обработки уведомлений;
* Проверка интеграции с Kafka и сериализации сообщений.  

## Требования  
* Java 17+
* Apache Kafka (локально или доступный удаленно)
* Maven 3.6+

## Конфигурация  
Настройки в application.yml:  
```yaml
server:
  port: 8081

spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    topic:
      notifications: notifications.topic
```
#### Основные параметры:

|Параметр	|Описание	|По умолчанию|  
|:--------|:-------------|:-----------|
|server.port	|Порт для REST API	|8081|
|spring.kafka.bootstrap-servers	|Адреса Kafka брокеров	|localhost:9092|
|spring.kafka.topic.notifications	|Топик для уведомлений	|notifications.topic|  

## Использование  
#### Автоматическая отправка уведомления  
При старте приложения автоматически отправляется тестовое уведомление с:
* Данными о подтверждении заказа 
* Получателем с email, телефоном и токенами устройств 
* Контентом для всех каналов (email, SMS, push, web)
* Метаданными и ссылками для действий

## REST API  
#### Отправить кастомное уведомление  
```text
POST /api/notifications
Content-Type: application/json
```

Тело запроса (NotificationDto в JSON):  
```json
{
  "notificationId": "test-test-test-test",
  "createdAt": "2025-07-17T14:30:00",
  "sender": {
    "system": "Payment System",
    "userId": "test-user"
  },
  "clientId": "test-client",
  "message": "Кредит по заявке 12345 одобрен"
}
```

Успешный ответ:  
```text
Notification sent with ID: 123e4567-e89b-12d3-a456-426614174000
```

## Разработка  

### Основные компоненты
* NotificationProducer — отправка сообщений в Kafka 
* TestDataGenerator — создание тестовых уведомлений 
* NotificationController — REST API endpoint

## Мониторинг  
Приложение логирует все операции с Kafka:  
* Успешные отправки включают метаданные 
* Ошибки содержат детали 
* REST API вызовы логируются

#### Пример лога:  
```text
INFO  o.s.kafka.core.KafkaTemplate - Отправка уведомления в топик notifications.topic
INFO  c.e.p.NotificationProducer - Уведомление успешно отправлено: 123e4567...
DEBUG c.e.p.NotificationProducer - Метаданные: топик notifications.topic, партиция 0, смещение 42
```

## Поиск проблем  
Возможные проблемы и решения:  

| Проблема	                                                    | Решение                                                 |  
|:-------------------------------------------------------------|:--------------------------------------------------------|  
| Нет соединения с Kafka	                                      | Проверьте работу Kafka и настройки bootstrap-servers    |
| Ошибки сериализации JSON	                                    | Убедитесь в наличии геттеров/сеттеров в NotificationDto |
| Топик не существует	| Создайте топик или включите автосоздание                |
|REST API не отвечает	|Проверьте параметр server.port|  

## Установка  
1. Клонируйте репозиторий:  
```bash
git clone https://github.com/your-repo/notification-system.git
```
2. Перейдите в модуль:
```bash
cd notification-system/kafka-test-producer
```
3. Соберите проект:  
```bash
mvn clean install
```  

## Запуск приложения  
1. Запустите Kafka (если не запущен)
2. Запустите приложение:  
```bash
java -jar target/kafka-test-producer-1.0-SNAPSHOT.jar
```  