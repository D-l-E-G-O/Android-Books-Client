import { prisma } from '../db';
import type { Request, Response } from 'express';
import { Prisma } from '../generated/prisma/client';

import { HttpError, NotFoundError } from '../error';

import { assert } from 'superstruct';
import { CommentCreationData, CommentUpdateData } from '../validation/comment';

export async function get_all_of_book(req: Request, res: Response) {
    const book = await prisma.book.findUnique({
        where: {
            id: Number(req.params.book_id)
        },
        include: {
            comments: true
        }
    });
    if (!book) {
        throw new NotFoundError('Book not found');
    }
    res.json(book.comments);
};

export async function create_one_of_book(req: Request, res: Response) {
    assert(req.body, CommentCreationData);
    // userId is expected here through the x-user-id header (instead of an authorization token)
    // the body only contains data needed to create a comment
    const userdId = req.headers['x-user-id'] ? Number(req.headers['x-user-id']) : undefined;
    if (!userdId) {
        throw new HttpError('User required', 401);
    }
    try {
        const comment = await prisma.comment.create({
            data: {
                ...req.body,
                user: {
                    connect: {
                        // userId is used here to connect the comment to the user declared in the header
                        id: Number(userdId)
                    }
                },
                book: {
                    connect: {
                        id: Number(req.params.book_id)
                    }
                }
            }
        });
        res.status(201).json(comment);
    }
    catch (err: unknown) {
        if (err instanceof Prisma.PrismaClientKnownRequestError && err.code === 'P2025') {
            throw new NotFoundError('Book or User not found');
        }
        throw err;
    }
};

export async function update_one(req: Request, res: Response) {
    assert(req.body, CommentUpdateData);
    // userId is expected here through the x-user-id header (instead of an authorization token)
    // the body only contains data needed to update a comment
    const userId = req.headers['x-user-id'] ? Number(req.headers['x-user-id']) : undefined;
    if (!userId) {
        throw new HttpError('User required', 401);
    }
    try {
        const comment = await prisma.comment.update({
            where: {
                id: Number(req.params.comment_id),
                // userId is used here to ensure that the updated comment belongs to the user declared in the header
                userId: Number(userId)
            },
            data: req.body
        });
        res.json(comment);
    }
    catch (err: unknown) {
        if (err instanceof Prisma.PrismaClientKnownRequestError && err.code === 'P2025') {
            throw new NotFoundError('Comment not found');
        }
        throw err;
    }
};

export async function delete_one(req: Request, res: Response) {
    // userId is expected here through the x-user-id header (instead of an authorization token)
    const userId = req.headers['x-user-id'] ? Number(req.headers['x-user-id']) : undefined;
    if (!userId) {
        throw new HttpError('User required', 401);
    }
    try {
        await prisma.comment.delete({
            where: {
                id: Number(req.params.comment_id),
                // userId is used here to ensure that the deleted comment belongs to the user declared in the header
                userId: Number(userId)
            }
        });
        res.status(204).send();
    }
    catch (err: unknown) {
        if (err instanceof Prisma.PrismaClientKnownRequestError && err.code === 'P2025') {
            throw new NotFoundError('Comment not found');
        }
        throw err;
    }
}
