## Описание процесса сдачи лабораторных

### Правила работы с репозиторием

- Вся работа ведется в вашей персональной ветке`
	- Избегайте использования символов `/ , \ . | * +`
	- Не забывайте по завершении работы делать push в удаленную ветку
- По возможности старайтесь сделать красивую историю коммитов в ней
- **ЗАПРЕЩАЕТСЯ ОПРАВЛЯТЬ ИЗМЕНЕНИЯ В ВЕТКУ main**
	- Все ваши коммиты из ветки main будут удалены
	- Кара воспоследует
- Выполняете работу в **исходных файлах с заданиями**, там есть шаблон кода и описание, при этом **НЕ НУЖНО:**
	- Копировать эти файлы в ту же директорию
	- Создавать отдельную директорию для решиний
	- Переименовывать эти файлы
	- Перименовывать и менять сигнатуру методов шаблона кода внутри файла

### Отметки о сдаче задания

- Выполнив задание и отправив его код в свою ветку в репозиторий, отметьте соответствующий чекбокс в [Таблице с результатами](https://docs.google.com/spreadsheets/d/1ly-yUu19S_mU7tSG_QLFaJOkQIM-uzzmXwLm4BGYW8Q)
- После проверки преподавателем в этой таблице будет отмечено выполнение задание:
	- Зеленый - все хорошо
	- Желтый - принято, но можно лучше
	- Оранжевый - есть замечания, требующие исправления
	- Красный - задание не найдено, задание не выполнено, задание списано
	- Будет добавлен комментарий с замечаниями, после исправления замечаний стоит добавить свой комментарий в трек
- Во всех задания Scala запрещено использование `var`

### Задания

#### Scala  - 1

- В данном задании необходимо реализовать простые функции, используя рекурсию (допускается использование дополнительных "внутренних" функции)
- Функции
	- `def pascal(c: Int, r: Int): Int` - функция возвращающая значение элемента треугольника Паскаля по номеру колонки и строки
  
	![треугольник Паскаля](https://upload.wikimedia.org/wikipedia/commons/7/71/%D0%A2%D1%80%D0%B5%D1%83%D0%B3%D0%BE%D0%BB%D1%8C%D0%BD%D0%B8%D0%BA_%D0%9F%D0%B0%D1%81%D0%BA%D0%B0%D0%BB%D1%8F.png)
	- ` def balance(chars: List[Char]): Boolean` - функция, подсчитывающая баланс скобок в выражении (по открытым и закрытым скобкам)
	- `def countChange(money: Int, coins: List[Int])` - функция, определяющая количество возможных вариантов размена суммы (`money`) монетами номиналом (`coins`)

#### Scala  - 2

- В это задании необходимо реализовать множество целых числе заданное как функцию `type Set = Int => Boolean`, данная функция отвечает на вопрос "содержится ли заданный элемент в множестве", выделяя его таким образом из множества целых чисел
- Для выполнения задания необходимо представить свою реализацию функций, тело которых представлено как `???`
- После выполнения задания, подумайте над тем, где все-таки хранятся элементы множества

#### Scala - 3

##### 3.1. N-Queens promblem

Write a recursive function nQueens that returns all solutions to the N-Queens problem for a given board size N.

```scala
nQueens(4) // Output: List of solutions (position sets for queens on a 4x4 board)
```

##### 3.2. Flatten a Nested List

Write a recursive function flatten that flattens a nested list into a single list:

```
flatten(List(List(1, 2), 3, List(4, List(5, 6))))  // Output: List(1, 2, 3, 4, 5, 6)
flatten(List(1, List(2, List(3))))                // Output: List(1, 2, 3)
```

#### Scala - 4

##### 4.1. Vector Operations (3D)

Implement a Vector3D class using case classes to represent 3D vectors. Provide implementations for basic vector operations using functional programming principles:
•	Addition: v1 + v2
•	Substraction: v1 - v2
•	Multiplication: v * const
•	Scalar multiplication: v1 * v2
•	Transpose

```scala
val v1 = Vector3D(1, 2, 3)
val v2 = Vector3D(4, 5, 6)
```
##### 4.2. Map Function

Write a recursive implementation of map for lists without using built-in library functions. The function signature should be:

```scala
def customMap[A, B](list: List[A], f: A => B): List[B]

customMap(List(1, 2, 3), x => x * 2) // Output: List(2, 4, 6)
```

