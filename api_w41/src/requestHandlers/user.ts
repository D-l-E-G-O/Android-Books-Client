import { prisma } from '../db';
import type { Request, Response } from 'express';
import { Prisma } from '../generated/prisma/client';

import { HttpError } from '../error';

import { assert } from 'superstruct';
import { UserCreationData } from '../validation/user';

export async function signup(req: Request, res: Response) {
    assert(req.body, UserCreationData);
    try {
        const user = await prisma.user.create({
            data: req.body
        });
        res.status(201).json(user);
    }
    catch (err: unknown) {
        if (err instanceof Prisma.PrismaClientKnownRequestError && err.code === 'P2002') {
            throw new HttpError('Email already taken', 400);
        }
        throw err;
    }
};
