import express from 'express';
import type { Request, Response, NextFunction } from 'express';
import cors from 'cors';

import { assert, object, optional, refine, string, StructError } from 'superstruct';
import { isInt } from 'validator';

import { HttpError } from './error';

import * as author from './requestHandlers/author';
import * as book from './requestHandlers/book';
import * as tag from './requestHandlers/tag';
import * as user from './requestHandlers/user';
import * as comment from './requestHandlers/comment';
import * as rating from './requestHandlers/rating';

const app = express();
const port = 3000;

app.set('query parser', 'extended');

app.use(cors());
app.use((req: Request, res: Response, next: NextFunction) => {
    res.header('Access-Control-Expose-Headers', 'X-Total-Count');
    next();
});

const ReqParams = object({
    author_id: optional(refine(string(), 'int', (value) => isInt(value))),
    book_id: optional(refine(string(), 'int', (value) => isInt(value))),
    tag_id: optional(refine(string(), 'int', (value) => isInt(value)))
});
const validateParams = (req: Request, res: Response, next: NextFunction) => {
    assert(req.params, ReqParams);
    next();
}

app.use(express.json());

app.route('/authors')
    .get(author.get_all)
    .post(author.create_one);

app.route('/authors/:author_id')
    .all(validateParams)
    .get(author.get_one)
    .patch(author.update_one)
    .delete(author.delete_one);

app.route('/authors/:author_id/books')
    .all(validateParams)
    .get(book.get_all_of_author)
    .post(book.create_one_of_author);

app.route('/books')
    .get(book.get_all);

app.route('/books/:book_id')
    .all(validateParams)
    .get(book.get_one)
    .patch(book.update_one)
    .delete(book.delete_one);

app.route('/books/:book_id/tags')
    .all(validateParams)
    .get(tag.get_all_of_book);

app.route('/books/:book_id/comments')
    .all(validateParams)
    .get(comment.get_all_of_book)
    .post(comment.create_one_of_book); // requires x-user-id header

app.route('/books/:book_id/ratings')
    .all(validateParams)
    .get(rating.get_all_of_book)
    .post(rating.create_one_of_book); // requires x-user-id header

app.route('/books/:book_id/ratings/average')
    .all(validateParams)
    .get(rating.get_average_of_book);

app.route('/books/:book_id/tags/:tag_id')
    .all(validateParams)
    .post(tag.add_one_to_book)
    .delete(tag.remove_one_from_book);

app.route('/comments/:comment_id')
    .all(validateParams)
    .patch(comment.update_one) // requires x-user-id header
    .delete(comment.delete_one); // requires x-user-id header

app.route('/ratings/:rating_id')
    .all(validateParams)
    .patch(rating.update_one) // requires x-user-id header
    .delete(rating.delete_one); // requires x-user-id header

app.route('/tags')
    .get(tag.get_all)
    .post(tag.create_one);

app.route('/tags/:tag_id')
    .all(validateParams)
    .get(tag.get_one)
    .patch(tag.update_one)
    .delete(tag.delete_one);

app.post('/signup', user.signup);

app.use((err: HttpError, req: Request, res: Response, next: NextFunction) => {
    if (err instanceof StructError) {
        err.status = 400;
        err.message = `Bad value for field ${err.key}`;
    }
    res.status(err.status ?? 500).send(err.message);
});

const host = '0.0.0.0';
app.listen(port, host, () => {
    console.log(`App listening on port ${port}`);
});
