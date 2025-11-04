# 🛒 E-Commerce プラットフォーム

モダンなフルスタックEコマースアプリケーション。Spring Boot 3とNext.js 16を使用して構築されています。

## 📋 目次

- [概要](#概要)
- [技術スタック](#技術スタック)
- [機能](#機能)
- [前提条件](#前提条件)
- [インストール](#インストール)
- [環境変数の設定](#環境変数の設定)
- [起動方法](#起動方法)
- [プロジェクト構成](#プロジェクト構成)
- [API ドキュメント](#api-ドキュメント)
- [開発](#開発)
- [デプロイ](#デプロイ)
- [ライセンス](#ライセンス)

## 🎯 概要

このプロジェクトは、最新の技術スタックを使用して構築されたフルスタックのEコマースプラットフォームです。バックエンドにSpring Boot 3、フロントエンドにNext.js 16を採用し、Docker Composeで簡単にデプロイできます。

### 主な特徴

- 🔐 JWT認証とGoogle OAuth 2.0統合
- 💳 Stripe決済統合
- 📧 メール通知システム
- 🚀 Redisキャッシング
- 🐳 Docker対応
- 📱 レスポンシブデザイン
- 🎨 モダンなUI/UX（TailwindCSS使用）

## 🛠 技術スタック

### バックエンド

- **フレームワーク**: Spring Boot 3.2.0
- **言語**: Java 21
- **データベース**: PostgreSQL 15
- **キャッシュ**: Redis 7
- **認証**: JWT + OAuth 2.0
- **決済**: Stripe API
- **ビルドツール**: Maven
- **主要ライブラリ**:
  - Spring Data JPA
  - Spring Security
  - Spring Mail
  - Lombok
  - Jackson

### フロントエンド

- **フレームワーク**: Next.js 16.0.0
- **言語**: TypeScript 5
- **UIライブラリ**: React 19.2.0
- **スタイリング**: TailwindCSS 3.4
- **状態管理**: Zustand 4.5
- **フォーム**: React Hook Form 7.50
- **アニメーション**: Framer Motion 11.0
- **アイコン**: Lucide React
- **HTTPクライアント**: Axios 1.6

### インフラストラクチャ

- **コンテナ化**: Docker & Docker Compose
- **データベース管理**: Adminer
- **Redis管理**: Redis Commander

## ✨ 機能

### ユーザー機能

- ✅ ユーザー登録・ログイン
- ✅ Google OAuth認証
- ✅ プロフィール管理
- ✅ パスワードリセット
- ✅ 商品閲覧・検索
- ✅ カート管理
- ✅ 注文処理
- ✅ 注文履歴
- ✅ Stripe決済

### 管理者機能

- ✅ 商品管理（CRUD）
- ✅ カテゴリ管理
- ✅ 注文管理
- ✅ ユーザー管理
- ✅ 在庫管理

## 📦 前提条件

開発環境に以下がインストールされている必要があります：

- **Docker**: 20.10以上
- **Docker Compose**: 2.0以上
- **Java**: 21以上（ローカル開発の場合）
- **Node.js**: 20以上（ローカル開発の場合）
- **Maven**: 3.8以上（ローカル開発の場合）

## 🚀 インストール

### 1. リポジトリのクローン

```bash
git clone https://github.com/yourusername/E-Commerce.git
cd E-Commerce
```

### 2. 環境変数ファイルの作成

プロジェクトルートに `.env` ファイルを作成します：

```bash
cp .env.example .env
```

## 🔧 環境変数の設定

`.env` ファイルを編集して、以下の環境変数を設定します：

```env
# メール設定
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# JWT設定
JWT_SECRET=your-256-bit-secret-key-for-jwt-tokens

# Stripe設定
STRIPE_PUBLIC_KEY=pk_test_your_stripe_public_key
STRIPE_SECRET_KEY=sk_test_your_stripe_secret_key

# Google OAuth設定
GOOGLE_CLIENT_ID=your-google-oauth-client-id
GOOGLE_CLIENT_SECRET=your-google-oauth-client-secret

# NextAuth設定
NEXTAUTH_SECRET=your-secret-key-for-nextauth
```

### 環境変数の取得方法

#### Gmail App Password
1. Googleアカウントの設定にアクセス
2. セキュリティ → 2段階認証プロセスを有効化
3. アプリパスワードを生成

#### Stripe API Keys
1. [Stripe Dashboard](https://dashboard.stripe.com/)にログイン
2. 開発者 → APIキーからテストキーを取得

#### Google OAuth Credentials
1. [Google Cloud Console](https://console.cloud.google.com/)にアクセス
2. プロジェクトを作成
3. APIとサービス → 認証情報 → OAuth 2.0クライアントIDを作成

## 🏃 起動方法

### Dockerを使用した起動（推奨）

```bash
# すべてのサービスを起動
docker-compose up -d

# ログを確認
docker-compose logs -f

# 管理ツールも含めて起動
docker-compose --profile tools up -d
```

サービスが起動したら、以下のURLにアクセスできます：

- **フロントエンド**: http://localhost:3000
- **バックエンドAPI**: http://localhost:8080/api
- **Adminer（DB管理）**: http://localhost:8081
- **Redis Commander**: http://localhost:8082

### ローカル開発環境での起動

#### バックエンド

```bash
cd ecommerce-backend
mvn clean install
mvn spring-boot:run
```

#### フロントエンド

```bash
cd ecommerce-frontend/src
npm install
npm run dev
```

## 📁 プロジェクト構成

```
E-Commerce/
├── ecommerce-backend/          # Spring Bootバックエンド
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/ecommerce/
│   │   │   │       ├── config/        # 設定クラス
│   │   │   │       ├── controller/    # RESTコントローラー
│   │   │   │       ├── dto/           # データ転送オブジェクト
│   │   │   │       ├── entity/        # JPAエンティティ
│   │   │   │       ├── repository/    # データリポジトリ
│   │   │   │       ├── service/       # ビジネスロジック
│   │   │   │       ├── security/      # セキュリティ設定
│   │   │   │       └── exception/     # 例外処理
│   │   │   └── resources/
│   │   │       └── application.yml    # アプリケーション設定
│   │   └── test/                      # テストコード
│   ├── Dockerfile
│   └── pom.xml                        # Maven設定
│
├── ecommerce-frontend/         # Next.jsフロントエンド
│   ├── src/
│   │   ├── app/                       # Next.js App Router
│   │   ├── components/                # Reactコンポーネント
│   │   ├── lib/                       # ユーティリティ関数
│   │   ├── hooks/                     # カスタムフック
│   │   ├── store/                     # Zustand状態管理
│   │   ├── types/                     # TypeScript型定義
│   │   └── package.json
│   └── Dockerfile
│
├── docker-compose.yml          # Docker Compose設定
└── README.md                   # このファイル
```

## 📚 API ドキュメント

### 認証エンドポイント

```
POST   /api/auth/register      - ユーザー登録
POST   /api/auth/login         - ログイン
POST   /api/auth/refresh       - トークンリフレッシュ
POST   /api/auth/logout        - ログアウト
GET    /api/auth/google        - Google OAuth
```

### 商品エンドポイント

```
GET    /api/products           - 商品一覧取得
GET    /api/products/{id}      - 商品詳細取得
POST   /api/products           - 商品作成（管理者）
PUT    /api/products/{id}      - 商品更新（管理者）
DELETE /api/products/{id}      - 商品削除（管理者）
```

### 注文エンドポイント

```
GET    /api/orders             - 注文一覧取得
GET    /api/orders/{id}        - 注文詳細取得
POST   /api/orders             - 注文作成
PUT    /api/orders/{id}        - 注文更新
```

### カートエンドポイント

```
GET    /api/cart               - カート取得
POST   /api/cart/items         - カートに追加
PUT    /api/cart/items/{id}    - カート商品更新
DELETE /api/cart/items/{id}    - カートから削除
```

## 💻 開発

### バックエンド開発

```bash
# テスト実行
cd ecommerce-backend
mvn test

# パッケージビルド
mvn clean package

# 開発モードで起動
mvn spring-boot:run
```

### フロントエンド開発

```bash
# 開発サーバー起動
cd ecommerce-frontend/src
npm run dev

# ビルド
npm run build

# 本番モードで起動
npm run start

# Lint実行
npm run lint
```

### データベース管理

Adminerを使用してデータベースを管理：

1. http://localhost:8081 にアクセス
2. 以下の情報でログイン：
   - **システム**: PostgreSQL
   - **サーバー**: postgres
   - **ユーザー名**: ecommerce_user
   - **パスワード**: ecommerce_password
   - **データベース**: ecommerce_db

### Redis管理

Redis Commanderを使用してRedisを管理：

1. http://localhost:8082 にアクセス
2. キャッシュデータを確認・管理

## 🚢 デプロイ

### Dockerを使用した本番デプロイ

```bash
# 本番用イメージをビルド
docker-compose build

# 本番環境で起動
docker-compose up -d

# ヘルスチェック
docker-compose ps
```

### 環境別設定

本番環境では、以下の設定を推奨します：

1. 強力なJWT秘密鍵を使用
2. 本番用のStripe APIキーを設定
3. HTTPSを有効化
4. データベースのバックアップを設定
5. ログ監視を設定

## 🔒 セキュリティ

- JWT認証による安全なAPI通信
- パスワードのBcryptハッシュ化
- CORS設定
- SQL インジェクション対策（JPA使用）
- XSS対策
- CSRF保護

## 🧪 テスト

```bash
# バックエンドテスト
cd ecommerce-backend
mvn test

# フロントエンドテスト
cd ecommerce-frontend/src
npm test
```

## 🤝 コントリビューション

1. このリポジトリをフォーク
2. フィーチャーブランチを作成 (`git checkout -b feature/AmazingFeature`)
3. 変更をコミット (`git commit -m 'Add some AmazingFeature'`)
4. ブランチにプッシュ (`git push origin feature/AmazingFeature`)
5. プルリクエストを作成

## 📝 ライセンス

このプロジェクトはMITライセンスの下で公開されています。

## 👥 作成者

あなたの名前 - [@yourtwitter](https://twitter.com/yourtwitter)

プロジェクトリンク: [https://github.com/yourusername/E-Commerce](https://github.com/yourusername/E-Commerce)

## 🙏 謝辞

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Next.js](https://nextjs.org/)
- [TailwindCSS](https://tailwindcss.com/)
- [Stripe](https://stripe.com/)
- [Docker](https://www.docker.com/)

---

⭐ このプロジェクトが役に立った場合は、スターをつけていただけると嬉しいです！
