import { prisma } from '../db';
import type { Request, Response } from 'express';
import { Prisma } from '../generated/prisma/client';

import { HttpError, NotFoundError } from '../error';

import { assert } from 'superstruct';
import { RatingCreationData, RatingUpdateData } from '../validation/rating';

export async function get_all_of_book(req: Request, res: Response) {
    const book = await prisma.book.findUnique({
        where: {
            id: Number(req.params.book_id)
        },
        include: {
            ratings: true
        }
    });
    if (!book) {
        throw new NotFoundError('Book not found');
    }
    res.json(book.ratings);
};

export async function get_average_of_book(req: Request, res: Response) {
    const rating_avg = await prisma.rating.aggregate({
        where: {
            bookId: Number(req.params.book_id)
        },
        _avg: {
            value: true
        }
    });
    if (!rating_avg._avg.value) {
        throw new NotFoundError('Book not found');
    }
    res.json(rating_avg._avg.value);
}

export async function create_one_of_book(req: Request, res: Response) {
    assert(req.body, RatingCreationData);
    // userId is expected here through the x-user-id header (instead of an authorization token)
    // the body only contains data needed to create a rating
    const userId = req.headers['x-user-id'] ? Number(req.headers['x-user-id']) : undefined;
    if (!userId) {
        throw new HttpError('User required', 401);
    }
    try {
        const rating = await prisma.rating.create({
            data: {
                ...req.body,
                user: {
                    connect: {
                        // userId is used here to connect the rating to the user declared in the header
                        id: Number(userId)
                    }
                },
                book: {
                    connect: {
                        id: Number(req.params.book_id)
                    }
                }
            }
        });
        res.status(201).json(rating);
    }
    catch (err: unknown) {
        if (err instanceof Prisma.PrismaClientKnownRequestError && err.code === 'P2025') {
            throw new NotFoundError('Book not found');
        }
        if (err instanceof Prisma.PrismaClientKnownRequestError && err.code === 'P2002') {
            throw new HttpError('User already provided a Rating for this Book', 400);
        }
        throw err;
    }
};

export async function update_one(req: Request, res: Response) {
    // userId is expected here through the x-user-id header (instead of an authorization token)
    // the body only contains data needed to update a rating
    const userId = req.headers['x-user-id'] ? Number(req.headers['x-user-id']) : undefined;
    if (!userId) {
        throw new HttpError('User required', 401);
    }
    assert(req.body, RatingUpdateData);
    try {
        const rating = await prisma.rating.update({
            where: {
                id: Number(req.params.rating_id),
                // userId is used here to ensure that the updated rating belongs to the user declared in the header
                userId: Number(userId)
            },
            data: req.body
        });
        res.json(rating);
    }
    catch (err: unknown) {
        if (err instanceof Prisma.PrismaClientKnownRequestError && err.code === 'P2025') {
            throw new NotFoundError('Rating not found');
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
        await prisma.rating.delete({
            where: {
                id: Number(req.params.rating_id),
                // userId is used here to ensure that the deleted rating belongs to the user declared in the header
                userId: Number(userId)
            }
        });
        res.status(204).send();
    }
    catch (err: unknown) {
        if (err instanceof Prisma.PrismaClientKnownRequestError && err.code === 'P2025') {
            throw new NotFoundError('Rating not found');
        }
        throw err;
    }
}
