# Testing Documentation

This document describes the testing strategy and how to run tests for the Vocabulary Android app.

## Test Structure

The project includes comprehensive testing at multiple levels:

### Unit Tests (`src/test/`)

Located in `app/src/test/java/com/vocabulary/`

#### Data Model Tests
- **WordTest.kt** - Tests for Word data model
  - Difficulty level parsing
  - Category parsing
  - Synonym/Antonym list parsing

- **WordProgressTest.kt** - Tests for WordProgress data model
  - Accuracy calculation
  - New word detection
  - Default values

#### Repository Tests
- **VocabularyRepositoryTest.kt** - Tests for VocabularyRepository
  - CRUD operations
  - Progress tracking
  - Statistics calculation
  - Uses MockK for mocking DAOs

- **VocabularyRepositoryFlowTest.kt** - Flow-specific tests
  - Flow emissions
  - Flow operators
  - Reactive data updates
  - Uses Turbine for Flow testing

#### ViewModel Tests
- **MainViewModelTest.kt** - Tests for MainViewModel
  - StateFlow emissions
  - Loading states
  - Coroutine handling
  - Demonstrates testable ViewModel pattern

### Instrumented Tests (`src/androidTest/`)

Located in `app/src/androidTest/java/com/vocabulary/`

#### Database Tests
- **WordDaoTest.kt** - Tests for WordDao
  - Insert/Update/Delete operations
  - Query operations
  - Flow emissions
  - Search functionality

- **WordProgressDaoTest.kt** - Tests for WordProgressDao
  - Progress tracking
  - Statistics queries
  - Reactive updates

## Running Tests

### From Android Studio

#### Run All Unit Tests
1. Right-click on `app/src/test` folder
2. Select "Run Tests in 'test'"

#### Run All Instrumented Tests
1. Right-click on `app/src/androidTest` folder
2. Select "Run Tests in 'androidTest'"
3. Choose a device or emulator

#### Run Specific Test Class
1. Open the test file
2. Click the green arrow next to the class name
3. Select "Run [TestClassName]"

#### Run Single Test Method
1. Open the test file
2. Click the green arrow next to the test method
3. Select "Run [testMethodName]"

### From Command Line

#### Run All Unit Tests
```bash
./gradlew test
```

#### Run Unit Tests with Coverage
```bash
./gradlew testDebugUnitTest --coverage
```

#### Run All Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

#### Run Specific Test Class
```bash
# Unit test
./gradlew test --tests "com.vocabulary.data.model.WordTest"

# Instrumented test
./gradlew connectedAndroidTest --tests "com.vocabulary.data.dao.WordDaoTest"
```

## Test Dependencies

### Unit Testing
- **JUnit 4** - Testing framework
- **MockK** - Mocking framework for Kotlin
- **Truth** - Fluent assertions from Google
- **Turbine** - Testing library for Kotlin Flows
- **Coroutines Test** - Testing utilities for coroutines
- **Arch Core Testing** - For testing LiveData/ViewModel

### Instrumented Testing
- **AndroidX Test** - Android testing framework
- **Espresso** - UI testing framework
- **Room Testing** - Room database testing utilities

## Testing Best Practices

### 1. Use Coroutines Test Dispatcher
```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class MyTest {
    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Test
    fun myTest() = runTest {
        // Test coroutines here
    }
}
```

### 2. Test Flow with Turbine
```kotlin
@Test
fun testFlow() = runTest {
    repository.getAllWords().test {
        val emission = awaitItem()
        assertThat(emission).hasSize(5)
        awaitComplete()
    }
}
```

### 3. Use Truth for Assertions
```kotlin
// Instead of assertEquals
assertThat(result).isEqualTo(expected)

// Better error messages
assertThat(list).hasSize(3)
assertThat(list).contains(item)
assertThat(value).isTrue()
```

### 4. Mock with MockK
```kotlin
val mockDao = mockk<WordDao>()
coEvery { mockDao.getWordById(1) } returns testWord
coVerify { mockDao.getWordById(1) }
```

### 5. Use In-Memory Database for DAO Tests
```kotlin
database = Room.inMemoryDatabaseBuilder(
    context,
    VocabularyDatabase::class.java
).build()
```

## Test Coverage

To generate test coverage report:

```bash
./gradlew testDebugUnitTestCoverage
```

View the report at:
```
app/build/reports/coverage/test/debug/index.html
```

## Continuous Integration

Tests run automatically on:
- Pull requests
- Pushes to main branch
- Before creating releases

## Troubleshooting

### Tests Fail with "No instrumentation runner found"
Make sure you have:
```kotlin
testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
```
in your `defaultConfig` block.

### Coroutine Tests Hang
Make sure you're using `runTest` from `kotlinx-coroutines-test`:
```kotlin
import kotlinx.coroutines.test.runTest

@Test
fun myTest() = runTest {
    // Your test code
}
```

### Flow Tests Don't Complete
Use Turbine's `test` function and call `awaitComplete()`:
```kotlin
flow.test {
    awaitItem()
    awaitComplete()
}
```

## Future Improvements

- [ ] Add UI tests with Espresso
- [ ] Increase test coverage to 80%+
- [ ] Add integration tests
- [ ] Add screenshot testing
- [ ] Add performance tests
