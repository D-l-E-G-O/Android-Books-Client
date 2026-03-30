import { prisma } from '../db';
import type { Request, Response } from 'express';
import { Prisma } from '../generated/prisma/client';

import { NotFoundError } from '../error';

import { assert } from 'superstruct';
import { BookCreationData, BookUpdateData, BookGetAllQuery } from '../validation/book';

export async function get_all(req: Request, res: Response) {
  assert(req.query, BookGetAllQuery);
  const { title, include, skip, take } = req.query;
  const filter: Prisma.BookWhereInput = {};
  if (title) {
    filter.title = { contains: String(title) };
  }
  const assoc: Prisma.BookInclude = {};
  if (include === 'author') {
    assoc.author = { select: { id: true, firstname: true, lastname: true } };
  }
  assoc.tags = { select: { id: true, name: true } };
  const books = await prisma.book.findMany({
    where: filter,
    include: assoc,
    orderBy: { title: 'asc' },
    skip: skip ? Number(skip) : undefined,
    take: take ? Number(take) : undefined
  });
  const bookCount = await prisma.book.count({ where: filter });
  res.header('X-Total-Count', String(bookCount));
  res.json(books);
};

export async function get_one(req: Request, res: Response) {
  const book = await prisma.book.findUnique({
    where: {
      id: Number(req.params.book_id)
    }
  });
  if (!book) {
    throw new NotFoundError('Book not found');
  }
  res.json(book);
};

export async function get_all_of_author(req: Request, res: Response) {
  assert(req.query, BookGetAllQuery);
  const { title, skip, take } = req.query;
  const filter: Prisma.BookWhereInput = {};
  if (title) {
    filter.title = { contains: String(title) };
  }
  const author = await prisma.author.findUnique({
    where: {
      id: Number(req.params.author_id),
    },
    include: {
      books: {
        where: filter,
        skip: skip ? Number(skip) : undefined,
        take: take ? Number(take) : undefined
      }
    }
  });
  if (!author) {
    throw new NotFoundError('Author not found');
  }
  filter.authorId = author.id;
  const bookCount = await prisma.book.count({ where: filter });
  res.header('X-Total-Count', String(bookCount));
  res.json(author.books);
};

export async function create_one_of_author(req: Request, res: Response) {
  assert(req.body, BookCreationData);
  try {
    const book = await prisma.book.create({
      data: {
        ...req.body,
        author: {
          connect: {
            id: Number(req.params.author_id)
          }
        }
      }
    });
    res.status(201).json(book);
  }
  catch (err: unknown) {
    if (err instanceof Prisma.PrismaClientKnownRequestError && err.code === 'P2025') {
      throw new NotFoundError('Author not found');
    }
    throw err;
  }
};

export async function update_one(req: Request, res: Response) {
  assert(req.body, BookUpdateData);
  try {
    const book = await prisma.book.update({
      where: {
        id: Number(req.params.book_id)
      },
      data: req.body
    });
    res.json(book);
  }
  catch (err: unknown) {
    if (err instanceof Prisma.PrismaClientKnownRequestError && err.code === 'P2025') {
      throw new NotFoundError('Book not found');
    }
    throw err;
  }
};

export async function delete_one(req: Request, res: Response) {
  try {
    await prisma.book.delete({
      where: {
        id: Number(req.params.book_id)
      }
    });
    res.status(204).send();
  }
  catch (err: unknown) {
    if (err instanceof Prisma.PrismaClientKnownRequestError && err.code === 'P2025') {
      throw new NotFoundError('Book not found');
    }
    throw err;
  }
};
