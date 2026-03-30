import { object, string, integer, optional, size, refine, enums } from 'superstruct';
import { isInt } from 'validator';

export const BookCreationData = object({
  title: size(string(), 1, 50),
  publication_year: optional(integer())
});

export const BookUpdateData = object({
  title: optional(size(string(), 1, 50)),
  publication_year: optional(integer())
});

export const BookGetAllQuery = object({
  title: optional(string()),
  include: optional(enums(['author'])),
  skip: optional(refine(string(), 'int', (value) => isInt(value))),
  take: optional(refine(string(), 'int', (value) => isInt(value))),
});
