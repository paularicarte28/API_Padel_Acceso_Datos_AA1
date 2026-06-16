# API Mock - PadelZGZ (WireMock)

API Mock de PadelZGZ creada con WireMock Standalone.
Permite probar todos los endpoints sin necesidad de arrancar Spring Boot ni MariaDB.

---

## Requisitos

- Java 11 o superior

---

## Puesta en marcha

### 1. Descarga WireMock Standalone

Descarga el JAR desde:
https://repo1.maven.org/maven2/org/wiremock/wiremock-standalone/3.3.1/wiremock-standalone-3.3.1.jar

Colócalo en esta misma carpeta `wiremock/`.

### 2. Arranca la API Mock

Desde la carpeta `wiremock/`:

```
java -jar wiremock-standalone-3.3.1.jar --port 9090
```

La API Mock arranca en: **http://localhost:9090**

---

## Estructura

```
wiremock/
├── mappings/        <- Definen método, URL y código de respuesta
└── __files/         <- Contienen el cuerpo (body) de cada respuesta
```

Cada caso tiene su propio fichero de mapping y su fichero de respuesta:
- `mappings/postClub-ok.json` → caso OK del POST /clubs
- `__files/postClub-ok.json` → body de la respuesta OK
- `mappings/postClub-ko-400.json` → caso de error 400
- `__files/postClub-ko-400.json` → body del error 400

---

## Casos cubiertos

| Endpoint | OK | KO |
|----------|----|----|
| POST /auth/login | 200 | 401 |
| GET /clubs | 200 | - |
| GET /clubs/{id} | 200 | 404 |
| POST /clubs | 201 | 400 |
| PUT /clubs/{id} | 200 | 404 |
| PATCH /clubs/{id} | 200 | - |
| DELETE /clubs/{id} | 204 | 404 |
| GET /pistas | 200 | - |
| GET /pistas/{id} | 200 | 404 |
| POST /pistas/club/{id} | 201 | 400 |
| DELETE /pistas/{id} | 204 | - |
| GET /usuarios | 200 | - |
| GET /usuarios/{id} | 200 | 404 |
| POST /usuarios | 201 | 400 |
| DELETE /usuarios/{id} | 204 | - |
| GET /reservas | 200 | - |
| POST /reservas | 201 | 404 |
| DELETE /reservas/{id} | 204 | - |
| GET /torneos | 200 | - |
| POST /torneos/club/{id} | 201 | - |
| DELETE /torneos/{id} | 204 | - |
| GET /inscripciones | 200 | - |
| POST /inscripciones | 201 | 404 |
| DELETE /inscripciones/{id} | 204 | - |
| GET /valoraciones | 200 | - |
| POST /valoraciones/pista/{id}/usuario/{id} | 201 | 400 |
| DELETE /valoraciones/{id} | 204 | - |

---

## Probar con curl

```bash
# Login OK
curl -X POST http://localhost:9090/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"pau@padelzgz.com","password":"miPassword123"}'

# Login KO 401
curl -X POST http://localhost:9090/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"pau@padelzgz.com","password":"wrong"}'

# Listar clubs
curl http://localhost:9090/clubs

# Club no encontrado (404)
curl http://localhost:9090/clubs/99999

# Crear club OK (201)
curl -X POST http://localhost:9090/clubs \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Club Test","ciudad":"Zaragoza"}'

# Crear club KO (400 - sin nombre)
curl -X POST http://localhost:9090/clubs \
  -H "Content-Type: application/json" \
  -d '{"ciudad":"Zaragoza"}'
```
