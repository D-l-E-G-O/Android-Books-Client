import { PrismaClient } from '../src/generated/prisma/client';
import { PrismaLibSql } from "@prisma/adapter-libsql";

const adapter = new PrismaLibSql({
    url: process.env.DATABASE_URL || ''
});
const prisma = new PrismaClient({ adapter });

const tags_data = [
    { name: 'Fantasy' }, { name: 'Horror' }, { name: 'Science Fiction' },
    { name: 'Dystopian' }, { name: 'Romance' }, { name: 'Thriller' },
    { name: 'Mystery' }, { name: 'Historical Fiction' }, { name: 'Cyberpunk' },
    { name: 'Adventure' }, { name: 'Biography' }, { name: 'Philosophy' },
    { name: 'Classic' }, { name: 'Young Adult' }, { name: 'Poetry' },
    { name: 'Drama' }, { name: 'Comedy' }, { name: 'Crime' },
    { name: 'Non-fiction' }, { name: 'Magic Realism' }
];

const authors_data = [
    {
        firstname: "H. P.", lastname: "Lovecraft",
        books: [
            { title: "The Call of Cthulhu", publication_year: 1928, tags: ["Horror", "Fantasy"] },
            { title: "At the Mountains of Madness", publication_year: 1936, tags: ["Horror", "Science Fiction"] }
        ]
    },
    {
        firstname: "J. R. R.", lastname: "Tolkien",
        books: [
            { title: "The Fellowship of the Ring", publication_year: 1954, tags: ["Fantasy", "Adventure", "Classic"] },
            { title: "The Hobbit", publication_year: 1937, tags: ["Fantasy", "Adventure"] }
        ]
    },
    {
        firstname: "Isaac", lastname: "Asimov",
        books: [
            { title: "Foundation", publication_year: 1951, tags: ["Science Fiction", "Classic"] },
            { title: "I, Robot", publication_year: 1950, tags: ["Science Fiction"] }
        ]
    },
    {
        firstname: "Frank", lastname: "Herbert",
        books: [
            { title: "Dune", publication_year: 1965, tags: ["Science Fiction", "Adventure"] },
            { title: "Dune Messiah", publication_year: 1969, tags: ["Science Fiction"] }
        ]
    },
    {
        firstname: "George", lastname: "Orwell",
        books: [
            { title: "1984", publication_year: 1949, tags: ["Dystopian", "Science Fiction", "Classic"] },
            { title: "Animal Farm", publication_year: 1945, tags: ["Dystopian", "Classic", "Philosophy"] }
        ]
    },
    {
        firstname: "Ray", lastname: "Bradbury",
        books: [
            { title: "Fahrenheit 451", publication_year: 1953, tags: ["Dystopian", "Science Fiction"] },
            { title: "The Martian Chronicles", publication_year: 1950, tags: ["Science Fiction"] }
        ]
    },
    {
        firstname: "Mary", lastname: "Shelley",
        books: [
            { title: "Frankenstein", publication_year: 1818, tags: ["Horror", "Science Fiction", "Classic"] }
        ]
    },
    {
        firstname: "Bram", lastname: "Stoker",
        books: [
            { title: "Dracula", publication_year: 1897, tags: ["Horror", "Classic"] }
        ]
    },
    {
        firstname: "Arthur", lastname: "Conan Doyle",
        books: [
            { title: "A Study in Scarlet", publication_year: 1887, tags: ["Mystery", "Crime", "Classic"] },
            { title: "The Hound of the Baskervilles", publication_year: 1902, tags: ["Mystery", "Crime"] }
        ]
    },
    {
        firstname: "Agatha", lastname: "Christie",
        books: [
            { title: "And Then There Were None", publication_year: 1939, tags: ["Mystery", "Crime", "Thriller"] },
            { title: "Murder on the Orient Express", publication_year: 1934, tags: ["Mystery", "Crime"] }
        ]
    },
    {
        firstname: "Stephen", lastname: "King",
        books: [
            { title: "The Shining", publication_year: 1977, tags: ["Horror", "Thriller"] },
            { title: "It", publication_year: 1986, tags: ["Horror", "Fantasy"] }
        ]
    },
    {
        firstname: "George R. R.", lastname: "Martin",
        books: [
            { title: "A Game of Thrones", publication_year: 1996, tags: ["Fantasy", "Adventure"] },
            { title: "A Clash of Kings", publication_year: 1998, tags: ["Fantasy", "Adventure"] }
        ]
    },
    {
        firstname: "Neil", lastname: "Gaiman",
        books: [
            { title: "American Gods", publication_year: 2001, tags: ["Fantasy", "Magic Realism"] },
            { title: "Coraline", publication_year: 2002, tags: ["Fantasy", "Horror", "Young Adult"] }
        ]
    },
    {
        firstname: "Terry", lastname: "Pratchett",
        books: [
            { title: "The Colour of Magic", publication_year: 1983, tags: ["Fantasy", "Comedy"] },
            { title: "Mort", publication_year: 1987, tags: ["Fantasy", "Comedy"] }
        ]
    },
    {
        firstname: "Douglas", lastname: "Adams",
        books: [
            { title: "The Hitchhiker's Guide to the Galaxy", publication_year: 1979, tags: ["Science Fiction", "Comedy"] }
        ]
    },
    {
        firstname: "Philip K.", lastname: "Dick",
        books: [
            { title: "Do Androids Dream of Electric Sheep?", publication_year: 1968, tags: ["Science Fiction", "Cyberpunk", "Dystopian"] },
            { title: "The Man in the High Castle", publication_year: 1962, tags: ["Science Fiction", "Historical Fiction"] }
        ]
    },
    {
        firstname: "Ursula K.", lastname: "Le Guin",
        books: [
            { title: "The Left Hand of Darkness", publication_year: 1969, tags: ["Science Fiction", "Philosophy"] }
        ]
    },
    {
        firstname: "Margaret", lastname: "Atwood",
        books: [
            { title: "The Handmaid's Tale", publication_year: 1985, tags: ["Dystopian", "Science Fiction", "Classic"] }
        ]
    },
    {
        firstname: "Jules", lastname: "Verne",
        books: [
            { title: "Twenty Thousand Leagues Under the Sea", publication_year: 1870, tags: ["Science Fiction", "Adventure", "Classic"] },
            { title: "Journey to the Center of the Earth", publication_year: 1864, tags: ["Science Fiction", "Adventure"] }
        ]
    },
    {
        firstname: "H. G.", lastname: "Wells",
        books: [
            { title: "The Time Machine", publication_year: 1895, tags: ["Science Fiction", "Classic"] },
            { title: "The War of the Worlds", publication_year: 1898, tags: ["Science Fiction", "Classic"] }
        ]
    },
    {
        firstname: "Victor", lastname: "Hugo",
        books: [
            { title: "Les Misérables", publication_year: 1862, tags: ["Classic", "Historical Fiction", "Drama"] }
        ]
    },
    {
        firstname: "Alexandre", lastname: "Dumas",
        books: [
            { title: "The Count of Monte Cristo", publication_year: 1844, tags: ["Classic", "Adventure", "Historical Fiction"] }
        ]
    },
    {
        firstname: "Jane", lastname: "Austen",
        books: [
            { title: "Pride and Prejudice", publication_year: 1813, tags: ["Classic", "Romance"] }
        ]
    },
    {
        firstname: "F. Scott", lastname: "Fitzgerald",
        books: [
            { title: "The Great Gatsby", publication_year: 1925, tags: ["Classic", "Drama"] }
        ]
    },
    {
        firstname: "Ernest", lastname: "Hemingway",
        books: [
            { title: "The Old Man and the Sea", publication_year: 1952, tags: ["Classic", "Adventure"] }
        ]
    },
    {
        firstname: "Mark", lastname: "Twain",
        books: [
            { title: "The Adventures of Tom Sawyer", publication_year: 1876, tags: ["Classic", "Adventure", "Young Adult"] }
        ]
    },
    {
        firstname: "Charles", lastname: "Dickens",
        books: [
            { title: "A Tale of Two Cities", publication_year: 1859, tags: ["Classic", "Historical Fiction"] }
        ]
    },
    {
        firstname: "Leo", lastname: "Tolstoy",
        books: [
            { title: "War and Peace", publication_year: 1869, tags: ["Classic", "Historical Fiction", "Philosophy"] }
        ]
    },
    {
        firstname: "Fyodor", lastname: "Dostoevsky",
        books: [
            { title: "Crime and Punishment", publication_year: 1866, tags: ["Classic", "Philosophy", "Crime"] }
        ]
    },
    {
        firstname: "Gabriel", lastname: "García Márquez",
        books: [
            { title: "One Hundred Years of Solitude", publication_year: 1967, tags: ["Classic", "Magic Realism"] }
        ]
    },
    {
        firstname: "Haruki", lastname: "Murakami",
        books: [
            { title: "Kafka on the Shore", publication_year: 2002, tags: ["Magic Realism", "Fantasy"] }
        ]
    }
];

const users_data = [
    {
        email: 'toto@hey.fr',
        username: 'toto',
        ratings: {
            create: [
                { value: 4, book: { connect: { id: 1 } } },
                { value: 5, book: { connect: { id: 2 } } },
                { value: 3, book: { connect: { id: 15 } } }
            ]
        },
        comments: {
            create: [
                { content: "Super livre, mais ça fait un peu peur", book: { connect: { id: 1 } } },
                { content: "Un chef d'œuvre absolu de la SF.", book: { connect: { id: 4 } } }
            ]
        },
    },
];

async function main() {
    console.log("--- Debut du Seeding ---");

    // 1. Nettoyage de la base de données
    console.log("Nettoyage des donnees existantes...");
    await prisma.comment.deleteMany();
    await prisma.rating.deleteMany();
    await prisma.book.deleteMany();
    await prisma.author.deleteMany();
    await prisma.tag.deleteMany();
    await prisma.user.deleteMany();

    // Réinitialisation des compteurs d'auto-incrémentation pour SQLite
    console.log("Reinitialisation des compteurs d'IDs...");
    await prisma.$executeRawUnsafe(`DELETE FROM sqlite_sequence;`);

    // 2. Création des Tags
    console.log("Création des tags...");
    for (const tag of tags_data) {
        await prisma.tag.create({
            data: tag
        });
    }

    // 3. Création des Auteurs et des Livres
    console.log("Création des auteurs et de leurs livres...");
    for (const authorData of authors_data) {
        const author = await prisma.author.create({
            data: {
                firstname: authorData.firstname,
                lastname: authorData.lastname,
            }
        });

        for (const book of authorData.books) {
            // Mappage des chaînes de caractères vers la syntaxe explicite de Prisma pour la table de jointure
            const tagsConnection = book.tags.map((tagName) => ({
                name: tagName
            }));

            await prisma.book.create({
                data: {
                    title: book.title,
                    publication_year: book.publication_year,
                    authorId: author.id,
                    tags: {
                        connect: tagsConnection
                    }
                }
            });
        }
    }

    // 4. Création des Utilisateurs, Notes et Commentaires
    console.log("Creation des utilisateurs et des interactions...");
    const books = await prisma.book.findMany();
    for (const user of users_data) {
        await prisma.user.create({
            data: user
        });
    }

    console.log("--- Seeding termine avec succes ! ---");
}

main()
    .then(async () => {
        await prisma.$disconnect();
    })
    .catch(async (e) => {
        console.error(e);
        await prisma.$disconnect();
        process.exit(1);
    });
