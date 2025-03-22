Решение описано после задания (Согласно тз).

Джарник и коллекция есть в сорсах и в релизе

Язык: Java

Инструменты:
OpenJDK 17
maven/gradle
Spring Boot
H2 Database

Дано:
Все звонки, совершенные абонентом сотового оператора, собираются на коммутаторах и фиксируются в CDR формате. Когда абонент находится в роуминге за процесс сбора его данных отвечает обслуживающая сеть абонента. Для стандартизации данных между разными операторами, международная ассоциация GSMA ввела стандарт BCE. Согласно ему, данные с CDR должны агрегировать в единый отчет UDR, который впоследствии передается оператору, обслуживающему абонента в домашней сети. На основе этого отчета, домашний оператор выставляет абоненту счет.

Цель задания – смоделировать описанный процесс в упрощенном виде.

Целевой микросервис будет генерировать CDR записи, сохранять их в базу данных и предоставлять Rest-API для получения UDR отчетов и генерации сводного отчета с CDR записями по абоненту.

CDR-запись включает в себя следующие данные:
  тип вызова (01 - исходящие, 02 - входящие);
 номер абонента, инициирующего звонок;
 номер абонента, принимающего звонок;
дата и время начала звонка (ISO 8601);
 дата и время окончания звонка (ISO 8601);
 
CDR-отчет представляет из себя набор CDR-записей.
разделитель данных – запятая;
разделитель записей – перенос строки;
данные обязательно формируются в хронологическом порядке;
в рамках задания CDR-отчет может быть обычным txt\csv;

Вот пример фрагмента CDR-отчета:
02,79876543221, 79123456789, 2025-02-10T14:56:12, 2025-02-10T14:58:20
01,79996667755, 79876543221, 2025-02-10T10:12:25, 2025-02-10T10:12:57
 
UDR представляет из себя объект в формате JSON, который включает в себя номер абонента и сумму длительности его звонков.

Пример UDR объекта
{
    "msisdn": "79992221122",
    "incomingCall": {
        "totalTime": "02:12:13"
    },
    "outcomingCall": {
        "totalTime": "00:02:50"
    }
}
 
Задача 1:
Напишите часть, эмулирующую работу коммутатора, т.е. генерирующую CDR записи.
Условия:
1.    Во время генерации создаются CDR записи и сохраняются в локальную БД (h2);
2.    Данные генерируются в хронологическом порядке звонков, т.е. записи по одному абоненту могут прерываться записями по другому абоненту;
3.    Количество и длительность звонков определяется случайным образом;
4.    Установленный список абонентов (не менее 10) хранится в локальной БД (h2);
5.    Один прогон генерации создает записи сразу за 1 год.

Задача 2:
Напишите часть, предоставляющую Rest-API для работы с UDR.
          	Условия:
1.  Требуется REST метод, который возвращает UDR запись (формат предоставлен выше) по одному переданному абоненту. В зависимости от переданных в метод параметров, UDR должен составляться либо за запрошенный месяц, либо за весь тарифицируемый период.
2.  Требуется REST метод, который возвращает UDR записи по всем нашим абонентам за запрошенный месяц.
3.  Данные можно брать только из БД.
   
Задача 3 (дополнительная):
Напишите часть, формирующую и сохраняющую CDR-отчет.
          	Условия:
1.   Напишите, REST метод, который инициирует генерацию CDR-отчета и возвращает успешный ответ (или текст ошибки) + уникальный UUID запроса, когда файл будет готов.
2.   CDR файл должен генерироваться для запрошенного абонента за переданный период времени. Переданный период может не совпадать с календарными месяцами. Например, можно запросить отчет по звонкам за две недели или за полгода.
3.   Данные можно брать только из БД.
4.   Сгенерированный файл может быть в формате csv или txt и располагаться в рабочей папке сервиса, в директории /reports.
5.   Название файла должно содержать номер пользователя и уникальный UUID запроса. Например – «79991112233_ 61f0c404-5cb3-11e7-907b-a6006ad3dba0.csv»

Решение: 

Архитектура разбита на слои бд, сервисов и контроллеров, для передачи данных между ними используются ДТО объекты.

Микросервис report предоставляет апи для работы с UDR, CDR отчетами:

GET /api/report/customers/all - возвращает номера пользователей

GET /api/report/udr/:phoneNumber?month=2024-03&fullPeriod=false - возвращает UDR отчет по пользователю за заданный период

GET /api/report/udr/all?month=2024-11 - возвращает UDR отчет по всем пользователям за заданный период

POST /api/report/cdr/:phoneNumber?start=2024-01-01T00:00:00Z&end=2024-12-30T23:59:59Z - генерирует CDR отчет в формате csv по заданному номеру и периоду и сохраняет в корне проекта в папке reports 

GET /api/report/cdr/status/:requestId - возвращает статус формирования отчета по его UUID

Для работы с апи к заданию прикреплена коллекция постман со всеми необходимыми настройками и ендпоинтами

Генерация данных происходит в момент обновления спринг контекста - когда он собран. 

Генераторы данных расположены в методах com.mobile.network.report.service.impl.CustomerServiceImpl#generateCustomers и для CDR com.mobile.network.report.service.impl.CDRRecordGeneratorServiceImpl#generateCDRRecords

CDR записи генерируются по переданному году, алгоритм генерации:
1. Получаем все номера пользователей из базы
2. Рандомизируется кол-во звонков в год от 100 до 599 totalCalls
3. Вычисляется кол-во секунд в году totalSecondsInYear = endOfYear - startOfYear
4. Вычисляется средний интервал между звонками averageIntervalSeconds = totalSecondsInYear / totalCalls
5. В цикле начиная с начала года вычисляется интервал, к среднему добавляется случайное отклонение
6. Проверяется что время не вышло за границы года
7. Генерируется одиночная запись CDRRecordDto
8. Получаем два случайных номера, если она совпадают, присваиваем второму новый номер пока они не будут разными
9. Рандомизируем CallType 01 или 02
10. Рандомизируем длительность разговора от 10 до 600 секунд
11. Длительность звонка вычисляется как сумма начала звонка и длительности разговора
12. Собранный объект добавляется в список для последующего сохранения

UDR отчеты формируются по алгоритму:
1. Создается вспомогательный объект-агрегатор длительности звонков, включающий поля длительность входящих и исходящих звонков
2. Расчитывается длительность звонка как разница между началом и концом звонка
3. Если тип звонка входящий (02) добавляется длительность к объекту агрегатору в поле длительность входящих, иначе в исходящую
4. Собирается и возвращается объект

Для формирования отчетов по списку абонентов используется тот же алгоритм, но для агрегации длительностей по конкретным номерам используется Map<номер, объект-агрегатор длительностей звонка>

В качестве базы используется H2, работа с ней происходит посредством Spring JPA

Из сторонних библиотек были использованы:

Mapstruct для уменьшения шаблонного кода генерации мапперов дто -> ентити

Lombok для уменьшения бойлерплейт кода

Улучшения в перспективе:

Одно из улучшений это при добавлении вариантов формирования отчета по CDR записям перейти на стратегию, чтобы подменять реализацию отталкиваясь от запроса.
