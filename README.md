Мой отчет посвящён подробному рассмотрению подготовленного мной проекта, связанного с разработкой собственной библиотеки реактивного программирования на платформе Java. 
Проект охватывает различные аспекты проектирования и разработки, начиная от определения общей архитектуры системы и заканчивая проведением полного цикла тестирования.

## Общая архитектура системы
Разработанная мною библиотека построена на принципах реактивного программирования и поддерживает разнообразные сценарии обработки асинхронных потоков данных. 

Ключевыми элементами структуры являются классы и интерфейсы, такие как:

    Observable: Источник потока данных, поддерживающий регистрацию подписчиков и отправку уведомлений.
    Observer: Интерфейс, определяющий поведение наблюдателей при получении данных.
    Disposable: Управляет состоянием подписки, позволяя пользователям контролировать её продолжительность.
    Schedulers: Набор инструментов для планирования исполнения задач в определённых потоках.

### 1. Базовые компоненты
**Реактивный поток** реализован через три ключевых элемента:

1. **Observable<T>**
    - Источник данных, реализующий паттерн "Наблюдатель"
    - Основные методы:
        - `create(OnSubscribe<T>)` - фабричный метод
        - `subscribe(Observer<T>)` - подписка на поток
        - Операторы преобразования (`map`, `filter` и др.)

2. **Observer<T>**
    - Интерфейс получателя данных:
      ```java
      void onNext(T item);  // Получение элемента
      void onError(Throwable t); // Обработка ошибки
      void onComplete(); // Уведомление о завершении
      void onSubscribe(Disposable d); // Управление подпиской
      ```

3. **Disposable**
    - Механизм управления жизненным циклом:
      ```java
      void dispose(); // Прерывание подписки
      boolean isDisposed(); // Проверка состояния
      ```

### 2. Операторы преобразования

| Оператор       | Назначение                          | Реализация                          |
|----------------|-------------------------------------|-------------------------------------|
| `map`          | Преобразование элементов            | Через `MapOperator`                 |
| `filter`       | Фильтрация элементов                | Через `FilterOperator`              |
| `flatMap`      | Асинхронное преобразование          | Через `FlatMapOperator`             |
| `subscribeOn`  | Задание потока для подписки         | Через обертку `OnSubscribe`         |
| `observeOn`    | Задание потока для обработки        | Через промежуточный `Observer`      |

### 3. Управление потоками (Schedulers)
   ```java
public interface Scheduler {
    void execute(Runnable task);
    Disposable schedule(Runnable task);
}
   ```

## Особенности реализации Schedulers
Рассматриваемые Schedulers играют ключевую роль в библиотеке, поскольку они управляют потоками выполнения операций и обеспечивают правильное распределение нагрузки. Выделяются три типа Schedulers:

    ComputationScheduler: Оптимален для высоко нагруженных вычислений.
    IOThreadScheduler: Подходит для задач ввода-вывода.
    SingleThreadScheduler: Обеспечивает строго последовательное исполнение операций.

Каждый из указанных Schedulers предназначен для специфичных целей и помогает эффективно решать конкретные задачи, улучшая общую производительность приложения.


### Принципы работы Schedulers
1. Типы планировщиков

| Scheduler               | Пул потоков                | Использование              |
|-------------------------|----------------------------|----------------------------|
| `IOThreadScheduler`     | CachedThreadPool           | I/O операции (сеть, файлы) |
| `ComputationScheduler`  | FixedThreadPool (ядра CPU) | Вычислительные задачи      |
| `SingleThreadScheduler` | SingleThreadExecutor       | Последовательная обработка |

2. Ключевые различия
   #### IOThreadScheduler 
    Оптимизирован для операций с ожиданием
    Автоматически масштабирует число потоков

    ##### Пример:
   ```java
    observable.subscribeOn(new IOThreadScheduler());
    ```

   #### ComputationScheduler
   Фиксированное число потоков (по ядрам CPU)
   Для CPU-интенсивных задач

   ##### Пример:
   ```java
    .observeOn(new ComputationScheduler())
    ```

   #### SingleThreadScheduler
   Гарантирует последовательное выполнение
   Полезен для обновления UI

   ##### Пример:
   ```java
    .observeOn(new SingleThreadScheduler())
    ```

Автоматически масштабирует число потоков
## Подход к процессу тестирования
Перед выпуском готовой версии библиотеки мной было проведено тестирование каждого компонента, включающее проверку функциональности классов, операторов и Schedulers. 

Среди используемых методов:

    Создание экземпляров Observable с последующим проверочным обсервингом полученных значений.
    Применение операторов преобразования и фильтрации данных.
    Тестирование эффективности и корректности работы с различными видами Schedulers.

Цель тестирования заключалась в выявлении возможных недостатков и проверке работоспособности библиотеки в условиях реальной эксплуатации.

Процесс тестирования

Стратегия тестирования
#### Юнит-тесты:

    Покрытие всех операторов
    Граничные случаи
    Многопоточные сценарии
   
#### Интеграционные тесты:
    Комбинации операторов
    Работа с Schedulers

Основные тест-кейсы
   
#### Тест операторов:

   ```java
@Test
public void testMapFilterChain() {
    Observable.just(1, 2, 3)
            .map(x -> x * 2)
            .filter(x -> x > 3)
            .test()
            .assertValues(4, 6);
}
   ```

#### Тест многопоточности:

   ```java
@Test
public void testThreadSwitching() {
    Observable.create(emitter -> {
                assertNotEquals(Thread.currentThread().getName(), "main");
                emitter.onNext(1);
            })
            .subscribeOn(new IOThreadScheduler())
            .test();
}
   ```

#### Тест отмены подписки:

   ```java
@Test
public void testDisposable() {
    Disposable disposable = Observable.interval(100)
            .subscribe(System.out::println);

    assertFalse(disposable.isDisposed());
    disposable.dispose();
    assertTrue(disposable.isDisposed());
}
   ```

## Практические примеры использования библиотеки
Помимо теоретической части отчета представляю практические примеры, демонстрирующие простоту и эффективность применения данной библиотеки в повседневных задачах разработчиков. 
   
Вот некоторые из них:

### 1. Создание Observable
```java
Observable.create((Observer<Integer> obs, Disposable d) -> {
    obs.onNext(1);
    obs.onNext(2);
    obs.onComplete();
}).subscribe(new Observer<Integer>() {
    @Override
    public void onNext(Integer item) {
        System.out.println("Получено: " + item);
    }
    // ... другие методы Observer
});
   ```

### 2. Операторы map и filter
```java
Observable.just(1, 2, 3)
    .map(x -> x * 2)
        .filter(x -> x > 3)
        .subscribe(System.out::println); // Вывод: 4, 6
   ```

### 3. Использование Schedulers
```java
Observable.create(emitter -> {
        // Выполняется в IOThreadScheduler
        emitter.onNext(1);
    emitter.onComplete();
})
        .subscribeOn(new IOThreadScheduler())
        .observeOn(new SingleThreadScheduler())
        .subscribe(System.out::println);
   ```

### 4. Отмена подписки
```java
Disposable disposable = Observable.interval(1000)
.subscribe(System.out::println);

// Через 5 секунд отменяем подписку
Thread.sleep(5000);
disposable.dispose();
   ```
