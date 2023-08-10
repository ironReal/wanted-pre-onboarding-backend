# 클라우드 배포 환경
![클라우드 배포 환경](img_1.png)
## AWS 배포 IP 주소
```text
 13.125.54.58
```
# 지원자의 성명
**박철진**
# 애플리케이션의 실행 방법 (엔드포인트 호출 방법 포함)
## 애플리케이션의 실행 방법
```text
$ ./gradlew build jar -x test
$ docker-compose up --build -d
```
## 엔드포인트 호출 방법
| HTTP Method | End point | Description |
| --- | --- | --- |
| POST | /api/member/join | 회원가입 |
| POST | /api/member/loging | 로그인 |
| POST | /api//post/register | 게시물 등록 |
| GET | /api//post/{Id} | 게시물 조회 |
| PUT | /api//post/update/{id} | 게시물 수정 |
| POST | /api//post/list | 게시물 목록 |
| DELETE | /api//post/{id} | 게시물 삭제 |

# 데이터베이스 테이블 구조
![데이터베이스 테이블 구조](데이터베이스%20구조.png)
# 구현한 API의 동작을 촬영한 데모 영상 링크
[데모 영상 링크(youtube)](https://www.youtube.com/watch?v=O6hCjmMgMR8&t=3s&ab_channel=%EB%B0%95%EC%B2%A0%EC%A7%84)
# 구현 방법 및 이유에 대한 간략한 설명
## 회원가입
- 전달된 JSON 타입을 MemberDTO 객체로 변환하여 회원가입을 진행합니다. 이때 Validation을 통해 객체가 email과 password 조건에 맞는지 검증합니다.
- 검증 된 객체는 Security에 설정된`BCryptPasswordEncoder`를 통해서 전달받은 비밀번호를 암호화하여 DB에 저장합니다.
## 로그인 성공 시 JWT 발행
- 로그인에 성공하였을 때 Access Token과 Refresh Token을 발행합니다. Acess Token은 기간을 1일로 지정해두었고 서버측에서 원하는 날짜를 조절 할 수 있습니다.
- 매 요청마다 Access Token의 유효기간을 검증하고, 유효기간이 지났을경우 Refresh Token을 통해서 새로운 Access Token 발급이 가능합니다. 
- Refresh Token의 만료 기간이 지났다면 다시 인증을 통해서 토큰들을 발급받아야 함을 전달합니다.
## 게시물 수정
- 작성자와 로그인한 멤버가 같아야 수정이 가능하므로 현재 로그인한 멤버를 찾기 위해 `SecurityContextHolder`에서 로그인한 멤버를 가져와 작성자의 email를 비교해서 검증합니다.
## 게시물 목록
- JPA에서 제공하는 Pageable를 이용해서 Pagination을 구현하였습니다. 전달 받은 URI를 통해서 원하는 커스텀이 가능합니다.
# API 명세(request/response 포함)
## 1. 회원가입
### 1.1 Request
- POST `/api/member/join`
    ```json
  {
      "email" : "wanted@google.com",
      "password" : "12345678"
    }
  ```
### 1.2 Response
- 201 Created
    ```json
  {
      wanted1@google.com
    }
  ```
- 400 Bad Request (이메일 검증)
    ```json
  {
      "code": "JOIN_INPUT_FORMAT",
      "message": "Email conditions include @ or password conditions are at least 8 characters",
      "errors": [
         {
             "field": "email",
             "message": "Email format is not correct"
          }
       ]
    }
  ```
- 400 Bad Request (비밀번호 검증)
    ```json
    {
     "code": "JOIN_INPUT_FORMAT",
     "message": "Email conditions include @ or password conditions are at least 8 characters",
      "errors": [
          {
             "field": "password",
              "message": "Password must be at least 8 characters long"
          }
      ]
    }
    ```
## 2. 로그인
### 2.1 Request
- POST `/api/member/login`
    ```json
    {
      "email" : "wanted@google.com",
      "password" : "12345678"
    }
    ```
### 2.2 Response
- 200 OK
    ```json
    {
     "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2OTE3NjM0MzIsImlhdCI6MTY5MTY3NzAzMiwiZW1haWwiOiJ3YW50ZWRAZ29vZ2xlLmNvbSJ9.z_9AYzqi-D-WvhwO7PBRn_zfJtFyJVxAFudmhycFZeY",
     "refreshToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2OTI1NDEwMzIsImlhdCI6MTY5MTY3NzAzMiwiZW1haWwiOiJ3YW50ZWRAZ29vZ2xlLmNvbSJ9.KrZ4jCVLl7WD507BCmhmYIbuKX6xEbnZUwdBG_6EF14"
    }
    ```
- 401 Unauthorized
    ```json
    {
        "timestamp": "2023-08-10T14:17:55.440+00:00",
        "status": 401,
        "error": "Unauthorized",
        "path": "/api/member/login"
    }
    ```
## 3. 게시물 등록
### 3.1 Request
- POST `/api/post/register`
- Headers `Authorization: Bearer AccessToken `
    ```json
    {
        "title" : "wanted",
        "content" : "content"   
    }
    ```
### 3.2 Response
- 201 Created
    ```json
    {
        "created board id": 1
    }   
    ```
- 403 Forbidden (유효하지 않는 Access Token)
    ```json
    {
        "time": "Aug 10, 2023, 2:25:10 PM",
        "message": "BadSignature Token"
    }
    ```
## 3. 게시물 조회
### 3.1 Request
- GET `/api/post/{id}`
- Headers `Authorization: Bearer AccessToken `
### 3.2 Response
- 201 Created
    ```json
    {
        "id": 1,
        "title": "wanted",
        "content": "content"
    }   
    ```
- 400 Bad Request (등록되지 않는 게시물 조회 요청)
    ```json
    {
        "code": "INVALID_PARAMETER",
        "message": "Invalid parameter included"
    }
    ```  
- 403 Forbidden (유효하지 않는 Access Token)
    ```json
    {
        "time": "Aug 10, 2023, 2:25:10 PM",
        "message": "BadSignature Token"
    }
    ```
## 4. 게시물 목록 조회
### 4.1 Request
- GET `/api/post/list?page=0&size=5&sort=regDate,desc`
- Headers `Authorization: Bearer AccessToken `
### 4.2 Response
- 200 OK
  ```json
  {
      "content": [
          {
              "id": 2,
              "title": "wanted",
              "content": "content"
          },
          {
              "id": 1,
              "title": "wanted",
              "content": "content"
          }
      ],
      "pageable": {
          "sort": {
              "empty": false,
              "unsorted": false,
              "sorted": true
          },
          "offset": 0,
          "pageNumber": 0,
          "pageSize": 5,
          "paged": true,
          "unpaged": false
      },
      "totalElements": 2,
      "totalPages": 1,
      "last": true,
      "size": 5,
      "number": 0,
      "sort": {
          "empty": false,
          "unsorted": false,
          "sorted": true
      },
      "numberOfElements": 2,
      "first": true,
      "empty": false
  }
  ```
## 5. 게시물 수정
### 5.1 Request
- PUT `/api/post/update/{id}`
- Headers `Authorization: Bearer AccessToken `
  ```json
  {
      "title" : "updateTitle",
      "content" : "updateContent"
  }

### 5.2 Response
- 200 OK
  ```json
  {
      "id": 1,
      "title": "updateTitle",
      "content": "updateContent"
  }
  ```
- 400 Bad Request (등록되지 않는 게시물 조회 요청)
    ```json
    {
        "code": "INVALID_PARAMETER",
        "message": "Invalid parameter included"
    }
    ```  
- 403 Forbidden (유효하지 않는 Access Token)
    ```json
    {
        "time": "Aug 10, 2023, 2:25:10 PM",
        "message": "BadSignature Token"
    }
    ```
- 400 Bad Reqeust (작성자와 로그인한 사용자가 다른 경우)
  ```json
      {
          "code": "WRITER_MISS_MACH",
          "message": "Only the author of the post must be able to modify the post"
      }
  ```
## 6. 게시물 삭제
### 6.1 Request
- DELETE `/api/post/{id}`
- Headers `Authorization: Bearer AccessToken `
### 6.2 Response
- 200 OK
  ```json
  {
      "delete_board_id": 1
  }
  ```
- 400 Bad Request (등록되지 않는 게시물 조회 요청)
    ```json
    {
        "code": "INVALID_PARAMETER",
        "message": "Invalid parameter included"
    }
    ```  
- 403 Forbidden (유효하지 않는 Access Token)
    ```json
    {
        "time": "Aug 10, 2023, 2:25:10 PM",
        "message": "BadSignature Token"
    }
    ```
- 400 Bad Reqeust (작성자와 로그인한 사용자가 다른 경우)
  ```json
      {
          "code": "WRITER_MISS_MACH",
          "message": "Only the author of the post must be able to modify the post"
      }
  ```