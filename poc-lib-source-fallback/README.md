# poc-lib-source-fallback

Experimenting with Gradle on how to fallback to other dependency source if the primary source is not found.

In this example, primary dependency is from project named `lib-project` and the fallback is a file of the same library located at `./main-project/lib/lib-project-0.1.0.jar`.

### Testing

1. Run test `MainProjectApplicationIT` in `main-project`. The test expects output text to start with `(LOCAL)` (dependency is imported from local project `lib-project`).
2. Open file `main-project/settings.gradle` and change argument of method `includeProjectIfExist` to something else.
3. Rebuild project. The dependency should fallback to JAR file version.
4. Run test `MainProjectApplicationIT` again. The test should fail and actual result text should start with `(JAR)`.
