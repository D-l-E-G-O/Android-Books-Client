import { object, integer, optional, min, max } from 'superstruct';

export const RatingCreationData = object({
    value: max(min(integer(), 0), 5)
});

export const RatingUpdateData = object({
    value: optional(max(min(integer(), 0), 5))
});
