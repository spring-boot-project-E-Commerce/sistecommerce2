# 🚚 새 레포로 이사 가이드

> **왜?** 옛 레포(`sistecommerce`)에 빌드 산출물(`bin/`·`build/`)과 비밀(`wallet/`·`application-secret.yml`)이
> 커밋돼 버려서, **깨끗한 새 레포로 이사**합니다.
>
> **새 레포:** `https://github.com/spring-boot-project-E-Commerce/sistecommerce2.git`
> **통합 브랜치:** `dev`

---

## 0. 용어 — "자기 클론 폴더"

각자 컴퓨터에서 깃허브 레포를 `git clone` 해서 받아둔 **그 프로젝트 폴더**를 말합니다.
새로 만드는 게 아니라, **평소 작업하던 그 폴더(예: `C:\...\shop`)** 를 열어서 거기 터미널에서 명령을 치면 됩니다.

확인용:
```bash
git remote -v   # origin ... sistecommerce.git 이 나오면 거기가 맞음
```

---

## ⛔ 절대 원칙

> **옛 브랜치를 새 레포에 통째로 `push` 하지 말 것.**
> 각자 **자기가 작업한 소스 파일만** 깨끗한 새 `dev` 위에 다시 얹어서 PR 한다.

특히 사고 커밋(`00db280` — 빈 `.gitignore` + 쓰레기/비밀)을 **pull 받은 적 있는 사람**이
통째로 push하면, 쓰레기·비밀이 새 레포로 다시 따라옵니다.

---

## ✅ 이사 절차 (모두 동일)

자기 클론 폴더에서:

```bash
# 1. 새 레포를 원격으로 추가하고 받기
git remote add new https://github.com/spring-boot-project-E-Commerce/sistecommerce2.git
git fetch new

# 2. 깨끗한 새 dev 위에 작업 브랜치 생성
git switch -c feature/내기능 new/dev

# 3. 내가 만든/고친 '소스 파일만' 콕 집어 가져오기
#    (옛 작업 브랜치명 + 내가 건드린 경로들을 나열)
git checkout <옛작업브랜치> -- src/main/java/com/example/java/<내도메인>/ \
                              src/main/resources/templates/<내화면>/

# 4. ★검증★ 쓰레기·비밀 안 섞였는지 확인
git status
git diff --cached --stat
#  → bin/  build/  .gradle/  .settings/  wallet/  application-secret.yml
#     이게 하나라도 보이면 멈추고 그 경로 빼기

# 5. 커밋 → 새 레포로 push → PR
git add -A
git commit -m "feat: <내 기능> 이전"
git push new feature/내기능
# GitHub sistecommerce2 에서 new dev 로 PR
```

💡 **3번이 핵심:** `git checkout <브랜치> -- <경로>` 는 **적은 경로의 파일만** 가져옵니다.
`bin/`·비밀은 경로에 안 적으니 원천적으로 안 따라와요. 히스토리 엉킴도 없습니다.

---

## 👥 유형별 주의

| 유형 | 방법 |
|------|------|
| **사고 커밋 pull 안 받은 사람** (히스토리 깨끗) | 위 3번 그대로. 편하면 체리픽도 OK |
| **사고 커밋 pull 받은 사람** | **무조건 3번 "파일만 가져오기".** 통째 push·체리픽 금지 |
| **채팅 만든 사람 (사고 커밋 본인)** | 파일-콕집기로 `.../chat/` 소스 + React 채팅 파일 + 채팅 템플릿만 |

> ⚠️ 사고 커밋을 받은 사람은 `.gitignore`가 비어있던 상태로 빌드해서,
> **자기 커밋에도 `bin/`·`build/`가 또 박혀 있을 수** 있습니다. 그래서 체리픽도 위험 → 파일-콕집기가 안전.

---

## 🔐 비밀 파일 처리

- `application-secret.yml`, `wallet/` 는 **git에 올리지 말고** 각자 로컬에만 둔다.
  (새 레포 `.gitignore`가 이미 막고 있음 — 실수로 `add`만 안 하면 됨)
- 새 레포엔 값 비운 `application-secret.example.yml` 템플릿만 공유.
- 🔴 옛 레포에 노출된 키(DB / PG / Cloudinary 등)는 **재발급** 권장.
  (이사해도 옛 레포·히스토리엔 키가 남아 있음)

---

## 📋 운영 순서

1. **한 기능씩 차례로** PR → `dev` 머지 (한꺼번에 X)
2. 머지할 때마다 PR diff에서 **`bin/`·비밀 안 들어왔는지** 재확인
3. 다 모이면 그때부터 새 레포 `sistecommerce2` 가 정식 레포
