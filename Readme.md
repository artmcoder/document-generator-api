# Document generator api
Using this application, you can generate PDF documents, based on the electronic signature and the data that we transfer to it
### Request to generate document `/api/v1/documents/generate POST`

1). First param | DocumentDTO
```java
class DocumentDTO {
    String fullName;
    String date;
    int number;
    String documentText;
}
```
   
2). Second param | Signature (MultipartFile)