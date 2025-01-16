# AI Education Application Backend

Ovo je backend aplikacija za AI-podržano obrazovanje, koja omogućava generisanje obrazovnog sadržaja koristeći OpenAI API.

## Glavne Funkcionalnosti

- Generisanje obrazovnog sadržaja pomoću OpenAI API-ja
- CRUD operacije za obrazovne materijale
- Filtriranje i pretraživanje materijala
- Autentifikacija korisnika
- Detaljno logiranje operacija

## Korištene Tehnologije

- Java 17
- Spring Boot 3.2.1
- PostgreSQL
- OpenAI GPT-3.5 API
- Maven

## Konfiguracija

### Environment Varijable

Aplikacija zahtijeva sljedeće environment varijable:

- `DB_URL`: URL za PostgreSQL bazu
- `DB_USERNAME`: Korisničko ime za bazu
- `DB_PASSWORD`: Lozinka za bazu
- `OPENAI_SECRET`: OpenAI API ključ

### OpenAI Konfiguracija

OpenAI integracija je konfigurisana sa sljedećim parametrima:

```yaml
openai:
  model: gpt-3.5-turbo
  max-tokens: 1000
  temperature: 0.7
  timeout: 60
```

## API Endpointi

### Materijali

- `POST /api/materials`: Kreira novi materijal
- `GET /api/materials`: Dohvata sve materijale
- `GET /api/materials/{id}`: Dohvata specifični materijal
- `POST /api/materials/{id}/generate`: Generiše sadržaj za materijal
- `PUT /api/materials/{id}`: Ažurira materijal
- `DELETE /api/materials/{id}`: Briše materijal

## Razvoj

1. Klonirajte repozitorij
2. Podesite environment varijable
3. Pokrenite `mvn clean install`
4. Pokrenite aplikaciju sa `mvn spring-boot:run`
