import { object, string, optional, size, refine } from 'superstruct';
import validator from 'validator';

export const UserCreationData = object({
    email: refine(string(), 'email', (value) => validator.isEmail(value)),
    username: optional(size(string(), 3, 50))
});

export const UserUpdateData = object({
    email: optional(refine(string(), 'email', (value) => validator.isEmail(value))),
    username: optional(size(string(), 3, 50))
});
