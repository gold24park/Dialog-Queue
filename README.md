# Dialog-Queue
An impmentation of Dialog Queue in Android + Jetpack Compose

![image2](/chart.jpg)

# Example
![image](/image.gif)
```kotlin
val ageResult = dialogHandler.showDialogForResult(SelectAge)
ageResult.onSuccess { age ->
    dialogHandler.showDialog(
        ConfirmAge, ConfirmAge.Req(
            exp = age,
            onClickNavigate = {
                startActivity(Intent(context, SecondaryActivity::class.java))
            }
        )
    )
}
```

# Article
[안드로이드 - Dialog Queue 구현하기](https://jizard.tistory.com/519)


