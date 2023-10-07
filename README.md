# Dialog-Queue
An impmentation of Dialog Queue in Android + Jetpack Compose

# Example
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
