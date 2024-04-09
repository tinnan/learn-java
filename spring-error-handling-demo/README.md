Demo app for leaning Spring error handling (`@ExceptionHandler`, `@ControllerAdvice`).
Learning from [Understanding Spring’s @ControllerAdvice](https://medium.com/@jovannypcg/understanding-springs-controlleradvice-cd96a364033f) guide.

## API
Use postman collection provided in this project.
1. Get user - valid user names: `john_d`, `jame_s`.
2. Post \
   valid input:
   ```JSON
   {
     "content": "<Content without words: politics, terrorism, murder>"
   }
   ```
   input for ContentNotAllowedException
   ```JSON
   {
     "content": "This is an act of terrorism !"
   }
   ```
   input for Other error
   ```JSON
   {
     "content": "test_internal_error"
   }
   ```
3. Comment \
   valid input:
   ```JSON
   {
     "content": "<Content without words: politics, terrorism, murder>"
   }
   ```
   input for ContentNotAllowedException
   ```JSON
   {
     "content": "This is an act of terrorism !"
   }
   ```
   