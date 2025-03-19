## 프로젝트 상세내용
**시연영상**


<p>
 <img src ='https://github.com/user-attachments/assets/c351849f-f9eb-4e25-a419-c52ca1324828>
</p>
<aside>
**사용 기술 스택**

![image](https://github.com/user-attachments/assets/bcd657b9-74ba-41c8-93a5-870c09986334)
서비스 개발 
Spring Boot



![image](https://github.com/user-attachments/assets/6a0944ac-9a34-47d9-9e9e-fe5668ab70e0)

DB
postSQL

![image](https://github.com/user-attachments/assets/e65ee55d-e019-43db-91f7-aa25ed35792a)



배포 : Docker

</aside>

<aside>
아키텍처

![image](https://github.com/user-attachments/assets/120007c2-1b10-4f95-a64f-582fa6c1a259)


</aside>
</div>
<aside>
 API 명세서

# 교회 웹 애플리케이션 API 문서 (테이블 형식)

## 1. 관리자 인증 API

| 메소드 | URL | 설명 | 요청 파라미터 | 응답 |
| --- | --- | --- | --- | --- |
| GET | `/andio/login` | 관리자 로그인 페이지 | 없음 | 로그인 페이지 HTML |
| GET | `/andio` | 관리자 대시보드 | 없음 | 대시보드 HTML 또는 로그인 리다이렉션 |

## 2. 사진 앨범 및 갤러리 API

| 메소드 | URL | 설명 | 요청 파라미터 | 응답 |
| --- | --- | --- | --- | --- |
| GET | `/andio/gallery` | 앨범 목록 조회 | `page`, `size`, `keyword`, `year`, `category` | 앨범 목록 HTML |
| GET | `/andio/gallery/album/{id}` | 앨범 상세 조회 | 경로변수: `id` | 앨범 상세 HTML |
| GET | `/andio/gallery/album/add` | 앨범 추가 폼 | 없음 | 앨범 추가 폼 HTML |
| POST | `/andio/gallery/album/add` | 앨범 추가 처리 | `album`, `thumbnailFile`, `albumPhotos` | 저장된 앨범 리다이렉션 |
| GET | `/andio/gallery/album/{id}/edit` | 앨범 수정 폼 | 경로변수: `id` | 앨범 수정 폼 HTML |
| POST | `/andio/gallery/album/{id}/edit` | 앨범 수정 처리 | 경로변수: `id`, `album`, `thumbnailFile`, `removeThumbnail` | 수정된 앨범 리다이렉션 |
| POST | `/andio/gallery/album/delete/{id}` | 앨범 삭제 | 경로변수: `id` | 앨범 목록 리다이렉션 |
| GET | `/andio/gallery/album/{id}/photos` | 앨범 사진 관리 | 경로변수: `id` | 사진 관리 HTML |
| POST | `/andio/gallery/album/{id}/upload` | 사진 업로드 | 경로변수: `id`, `photoFiles` | 사진 관리 리다이렉션 |
| POST | `/andio/gallery/photo/{id}/update-caption` | 사진 캡션 업데이트 | 경로변수: `id`, 바디: `caption` | JSON 응답 |
| POST | `/andio/gallery/photo/{id}/delete` | 사진 삭제 | 경로변수: `id` | 앨범 사진 관리 리다이렉션 |
| POST | `/andio/gallery/album/{id}/delete-selected` | 선택한 사진 삭제 | 경로변수: `id`, `photoIds` | 앨범 사진 관리 리다이렉션 |
| POST | `/andio/gallery/album/{id}/reorder` | 사진 순서 변경 | 경로변수: `id`, 바디: 사진 ID 배열 | JSON 응답 |
| POST | `/andio/gallery/album/{albumId}/set-thumbnail/{photoId}` | 앨범 썸네일 설정 | 경로변수: `albumId`, `photoId` | JSON 응답 |

## 3. 공지사항 API

| 메소드 | URL | 설명 | 요청 파라미터 | 응답 |
| --- | --- | --- | --- | --- |
| GET | `/andio/notice` | 공지사항 목록 조회 | `page`, `size`, `keyword` | 공지사항 목록 HTML |
| GET | `/andio/notice/{id}` | 공지사항 상세 조회 | 경로변수: `id` | 공지사항 상세 HTML |
| GET | `/andio/notice/add` | 공지사항 작성 폼 | 없음 | 공지사항 작성 폼 HTML |
| POST | `/andio/notice/add` | 공지사항 등록 처리 | `notice`, `files`, 인증 정보 | 공지사항 목록 리다이렉션 |
| GET | `/andio/notice/edit/{id}` | 공지사항 수정 폼 | 경로변수: `id` | 공지사항 수정 폼 HTML |
| POST | `/andio/notice/edit/{id}` | 공지사항 수정 처리 | 경로변수: `id`, `notice`, `files`, `keepAttachments`, 인증 정보 | 공지사항 목록 리다이렉션 |
| POST | `/andio/notice/delete/{id}` | 공지사항 삭제 | 경로변수: `id` | 공지사항 목록 리다이렉션 |

## 4. 설교 API

| 메소드 | URL | 설명 | 요청 파라미터 | 응답 |
| --- | --- | --- | --- | --- |
| GET | `/andio/sermon` | 설교 목록 조회 | `page`, `size`, `keyword`, `year`, `preacher` | 설교 목록 HTML |
| GET | `/andio/sermon/add` | 설교 추가 페이지 | 없음 | 설교 추가 폼 HTML |
| GET | `/andio/sermon/edit/{id}` | 설교 수정 페이지 | 경로변수: `id` | 설교 수정 폼 HTML |
| GET | `/andio/sermon/{id}` | 설교 상세 보기 | 경로변수: `id` | 설교 상세 HTML |
| POST | `/andio/sermon/save` | 설교 저장 (추가/수정) | `sermon`, `thumbnail` | 설교 목록 리다이렉션 |
| POST | `/andio/sermon/delete/{id}` | 설교 삭제 | 경로변수: `id` | 설교 목록 리다이렉션 |

## 6. 소개 API

| 메소드 | URL | 설명 | 요청 파라미터 | 응답 |
| --- | --- | --- | --- | --- |
| GET | `/` | 홈페이지 | 없음 | 홈페이지 HTML |
| GET | `/about` | 교회 소개 페이지 | `menu` (기본값: "greeting") | 소개 페이지 HTML |
| GET | `/worship` | 예배 안내 페이지 | `menu` (기본값: "times") | 예배 안내 HTML |
| GET | `/sermon` | 설교 페이지 | `menu` (기본값: "recent"), `page`, `size`, `keyword` | 설교 페이지 HTML |
| GET | `/sermon/{id}` | 설교 상세 페이지 | 경로변수: `id` | 설교 상세 HTML |
| GET | `/news` | 교회 소식 페이지 | `menu` (기본값: "notice"), `page`, `size`, `year`, `month`, `category`, `keyword` | 교회 소식 HTML |
| GET | `/news/album/{id}` | 앨범 상세 페이지 | 경로변수: `id` | 앨범 상세 HTML |
| GET | `/news/notice/{id}` | 공지사항 상세 페이지 | 경로변수: `id` | 공지사항 상세 HTML |

</aside>

<aside>
DB Table

- 설교 테이블
    
    ```sql
    CREATE TABLE sermons (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    preacher VARCHAR(100) NOT NULL,
    bible_verse VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    thumbnail_url VARCHAR(255)
    );
    
    - - 인덱스 추가 (검색 최적화)
    CREATE INDEX idx_sermons_date ON sermons(date);
    CREATE INDEX idx_sermons_title ON sermons(title);
    CREATE INDEX idx_sermons_preacher ON sermons(preacher);
    ```
- 앨범
    
    ```sql
    //사진테이블
    - id: Long (PK)
    - albumId: Long (FK to PhotoAlbum)
    - imageUrl: String (Cloudinary URL)
    - publicId: String (Cloudinary Public ID)
    - caption: String (사진 설명)
    - displayOrder: Integer (표시 순서)
    - createdAt: DateTime (업로드일시)
    ```
    
    ```sql
    //앨범테이블
    - id: Long (PK)
    - title: String (앨범 제목)
    - description: String (앨범 설명)
    - eventDate: Date (행사 날짜)
    - category: String (카테고리)
    - thumbnailUrl: String (대표 이미지 URL)
    - createdAt: DateTime (생성일시)
    - updatedAt: DateTime (수정일시)
    ```
    
- 공지사항
    ```sql
    - - 공지사항 테이블
    CREATE TABLE notices (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100),
    view_count INTEGER NOT NULL DEFAULT 0,
    pinned BOOLEAN NOT NULL DEFAULT false,
    thumbnail_url VARCHAR(255)
    );
    - - 공지사항 첨부파일 테이블
    CREATE TABLE notice_attachments (
    id SERIAL PRIMARY KEY,
    notice_id BIGINT NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    CONSTRAINT fk_notice_attachments_notice
    FOREIGN KEY (notice_id)
    REFERENCES notices (id)
    ON DELETE CASCADE
    );
    - - 공지사항 로그 테이블 (수정 이력 추적용)
    CREATE TABLE notice_logs (
    id SERIAL PRIMARY KEY,
    notice_id BIGINT NOT NULL,
    username VARCHAR(100) NOT NULL,
    action VARCHAR(50) NOT NULL,
    description TEXT,
    timestamp TIMESTAMP NOT NULL,
    CONSTRAINT fk_notice_logs_notice
    FOREIGN KEY (notice_id)
    REFERENCES notices (id)
    ON DELETE CASCADE
    );
    - - 인덱스 생성
    CREATE INDEX idx_notices_pinned_created ON notices (pinned, created_at DESC);
    CREATE INDEX idx_notice_attachments_notice_id ON notice_attachments (notice_id);
    CREATE INDEX idx_notice_logs_notice_id ON notice_logs (notice_id);
    ```
</aside>

<aside>
 서비스 구현

</aside>

---

### 결과

<aside>
긍정적인 점

-여지것 배운것을 활용하여  제작부터 배포 까지 할 수 있었던 시간이었다.

-cloudinary 를 이용하여  이미지 파일을 url로 url을 db에 저장하는 방식으로 이미지를 저장하는 방식을 숙달되었다.

-배포 환경에 맞춰 docker  image와 스팩을 조절했던 경험이 알찬

</aside>

<aside>
 **아쉬웠던 점**

- 
    
    
- 
</aside>

---

### 프로젝트를 통해 얻은 것

<aside>


- 기존 지식을 정리하는 시간이었다.
- 개발 환경에 기반하여 설계를 해야하는 중요성에 대하여 배웠다.
</aside>
