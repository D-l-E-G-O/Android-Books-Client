# books-2-bun

To setup the environment:
```bash
cp .env.example .env
```

To install dependencies:

```bash
bun install
```

To seed:

```bash
bunx prisma db push
bunx prisma db seed
```

To reset the database and re-seed:
```bash
bunx prisma db push --force-reset
bunx prisma db seed
```

To run:

```bash
bun run dev
```

This project was created using `bun init` in bun v1.3.5. [Bun](https://bun.com) is a fast all-in-one JavaScript runtime.
