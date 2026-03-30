import { object, optional, refine, string, size, enums } from 'superstruct';
import { isInt } from 'validator';

export const AuthorCreationData = object({
  firstname: size(string(), 1, 50),
  lastname: size(string(), 1, 50),
});

export const AuthorUpdateData = object({
  firstname: optional(size(string(), 1, 50)),
  lastname: optional(size(string(), 1, 50)),
});

export const AuthorGetAllQuery = object({
  lastname: optional(string()),
  hasBooks: optional(enums(['true'])),
  include: optional(enums(['books'])),
  skip: optional(refine(string(), 'int', (value) => isInt(value))),
  take: optional(refine(string(), 'int', (value) => isInt(value))),
});
