import { object, string, size, optional } from 'superstruct';

export const CommentCreationData = object({
    content: size(string(), 1, 500)
});

export const CommentUpdateData = object({
    content: optional(size(string(), 1, 500))
});
