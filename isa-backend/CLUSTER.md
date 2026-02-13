# Rad u klasteru (3.11)

Aplikacija podržava rad sa **najmanje dve replike**, istu bazu, **load balancer** ispred API sloja i **health check** za proveru rada replika.

---

## Usklađenost sa specifikacijom 3.11

| Zahtev iz specifikacije | Kako je pokriveno | Kako demonstrirati |
|-------------------------|-------------------|---------------------|
| **Dve replike, ista baza** | Dve instance (8084, 8085) koriste istu bazu `isa` (localhost:5432/isa). U IntelliJ: Backend Replica 1 i Backend Replica 2; u Docker-u: backend-replica1 i backend-replica2. | Pokreni obe replike; pozovi `GET /api/videos` na 8084 i 8085 – ista lista (ista baza). |
| **Load balancer ispred API sloja** | LocalLoadBalancer (8080) u IntelliJ; Nginx (8080) u Docker. Round-robin na 8084 i 8085. | Pokreni Load Balancer + obe replike; više puta `GET http://localhost:8080/api/cluster/demo` – naizmenično replica1 i replica2. |
| **Mehanizmi za proveru rada replika (health check)** | Actuator `/actuator/health` (zavisi od baze). U Docker-u: healthcheck na replikama + Nginx `max_fails`/`fail_timeout`. | Bez Docker-a: `GET http://localhost:8084/actuator/health` i 8085. Sa Docker-om: Nginx isključuje repliku koja ne odgovori. |
| **Pad jedne replike – aplikacija ostaje funkcionalna** | Druga replika i dalje prima zahteve. Sa Java LB: zahtevi na 8080 ponekad uspevaju (replica1), ponekad 502 (kad pogodi replica2). Sa Docker Nginx: Nginx isključi neispravnu repliku, sve ide na drugu. | Zaustavi Backend Replica 2; pozivi na 8084 (ili kroz 8080 na replica1) i dalje rade. |
| **Ponovno podizanje replike** | Ponovo pokreni zaustavljenu repliku; učitava se u balansiranje. | Pokreni ponovo Backend Replica 2; `http://localhost:8080/api/cluster/demo` opet naizmenično na obe. |
| **Parcijalni gubitak konekcije prema MQ ili bazi** | **Baza:** `/actuator/health` vraća DOWN kada baza nije dostupna; replika se može isključiti iz balansiranja (Docker/Nginx). **MQ:** U projektu nema MQ; isti princip važi ako se doda (health indicator za MQ). | Simuliraj prekid baze za jednu repliku (npr. pogrešan DB URL u profilu); health za tu repliku = DOWN; zahtevi idu na drugu. |
| **Primer pozivom proizvoljnog API-ja** | **`GET /api/cluster/demo`** – vraća `replicaId`, `timestamp`, poruku. Dovoljno za dokaz rada klastera i load balancera. | `GET http://localhost:8080/api/cluster/demo` (više puta) – vidiš koja replika je odgovorila. Opciono: `GET /api/videos`. |

Sve tačke specifikacije 3.11 su pokrivene. Za demonstraciju bez Docker-a dovoljno je: glavna app (8081), Backend Replica 1 (8084), Backend Replica 2 (8085), Load Balancer (8080) i poziv `GET http://localhost:8080/api/cluster/demo`.

---

## Šta je implementirano

| Zahtev | Rešenje |
|--------|--------|
| Dve replike, ista baza | `docker-compose.cluster.yml`: `backend-replica1` i `backend-replica2`, obe koriste `postgres:5432/isa` |
| Load balancer ispred API-ja | Nginx u istom Compose-u, port 8080 → round-robin na replike |
| Health check replika | Spring Boot Actuator `/actuator/health` + Docker healthcheck + Nginx `max_fails`/`fail_timeout` |
| Pad jedne replike | Nginx isključi neispravnu repliku nakon 3 neuspeha (30s); zahtevi idu na drugu |
| Ponovno podizanje replike | `docker compose up -d backend-replica1` – replika se ponovo uključi u upstream |
| Parcijalni gubitak konekcije (baza) | Health zavisi od DB; kada DB nije dostupna, `/actuator/health` vraća DOWN, replika se isključuje iz balansiranja |

**Napomena za MQ:** Ako kasnije dodaš RabbitMQ/Kafka, dodaj `ManagementHealthIndicator` za konekciju ka MQ; isti princip – kada health padne, LB ne šalje više zahteve na tu repliku.

---

## Pokretanje replika iz IntelliJ (Edit Configurations)

Možeš pokretati dve replike direktno iz IDE, bez Docker komandi. Obe koriste **istu bazu** (localhost:5432/isa) – samo obezbedi da PostgreSQL radi.

### 1. Edit Configurations

**Run → Edit Configurations** (ili ikonica liste konfiguracija pored Run).

### 2. Prva konfiguracija – Backend Replica 1

- Klik **+** → **Application**.
- **Name:** `Backend Replica 1`
- **Main class:** `rs.ac.ftn.isa.backend.IsaBackendApplication`
- **Active profiles:** `cluster,cluster-replica1`
- Ostalo ostavi podrazumevano. **Apply**.

### 3. Druga konfiguracija – Backend Replica 2

- Ponovo **+** → **Application**.
- **Name:** `Backend Replica 2`
- **Main class:** `rs.ac.ftn.isa.backend.IsaBackendApplication`
- **Active profiles:** `cluster,cluster-replica2`
- **Apply**.

### 4. Treća konfiguracija – Load balancer (bez Docker-a)

- Ponovo **+** → **Application**.
- **Name:** `Load Balancer`
- **Main class:** `rs.ac.ftn.isa.backend.cluster.LocalLoadBalancer`
- Ostalo prazno (nema profila, nema Program arguments). **Apply** → **OK**.

Ova konfiguracija pokreće mali Java program koji sluša na **8080** i prosleđuje zahteve na 8084 i 8085 (round-robin). Sve iz IntelliJ, bez Docker-a.

### 5. Pokretanje

Pokreni redom: **Backend Replica 1**, **Backend Replica 2**, pa **Load Balancer**. (Opciono prvo i glavnu app na 8081 ako želiš iste podatke.) Svaka replika je na svom portu; load balancer na 8080:

- Replica 1: **http://localhost:8084** (npr. `http://localhost:8084/api/cluster/demo`)
- Replica 2: **http://localhost:8085** (npr. `http://localhost:8085/api/cluster/demo`)
- **Load balancer (round-robin):** **http://localhost:8080** – svi zahtevi na 8080 idu naizmenično na 8084 i 8085 (npr. `http://localhost:8080/api/cluster/demo`, `http://localhost:8080/api/videos`).

Baza je zajednička (iz `application.properties`: `localhost:5432/isa`). Load balancer možeš pokretati iz IntelliJ (konfiguracija "Load Balancer"); Docker ti ne treba za to.

**Važno za testiranje u IntelliJ:**  
Da bi na 8084 i 8085 videla iste podatke (npr. listu videa na `/api/videos`), **mora da bude pokrenuta i glavna aplikacija** (IsaBackendApplication na 8081). Replike koriste istu bazu `isa`, ali glavna aplikacija ima `spring.jpa.hibernate.ddl-auto=create-drop` – pri gašenju glavne app tabele se brišu, pa ako samo replike rade, baza može biti prazna. Zato prvo podigni **glavnu app (8081)**, pa zatim **Backend Replica 1** i **Backend Replica 2**; tada će sve tri instance čitati iz iste baze i videćeš iste videe na 8081, 8084 i 8085.

Ako nemaš **Active profiles**, u **Program arguments** stavi:  
`--spring.profiles.active=cluster,cluster-replica1` (za Replica 1) odnosno `cluster,cluster-replica2` (za Replica 2).

---

## Da li može bez Docker / docker-compose?

**Da.** Za samu logiku klastera (dve replike, ista baza) Docker ti ne treba:

- Pokreneš **PostgreSQL** (već ga imaš – lokalno ili kako god).
- U IntelliJ pokreneš **Backend Replica 1** i **Backend Replica 2**.
- Testiraš direktno na **8084** i **8085** (kako je opisano u "Testiranje bez Docker-a" ispod).

**Load balancer bez Docker-a:** U projektu postoji **LocalLoadBalancer** (treća IntelliJ konfiguracija). Pokreneš ga kao "Load Balancer" – sluša na 8080 i prosleđuje na 8084 i 8085 (round-robin). Nginx/Docker ti ne treba za osnovno testiranje load balancera.

**Docker / docker-compose ti treba samo ako hoćeš** scenarije sa Nginx-om (npr. da Nginx sam isključi neispravnu repliku nakon neuspeha) ili ceo stack u kontejnerima.

---

## Kako testirati

### Testiranje bez Docker-a (samo IntelliJ + lokalna baza)

Koristiš ovo kada replike pokrećeš iz IntelliJ i ne koristiš docker-compose.

1. **Baza i glavna aplikacija**  
   Obezbedi da PostgreSQL radi na `localhost:5432`, baza `isa`. **Podigni i glavnu aplikaciju** (IsaBackendApplication na 8081), jer ona koristi `create-drop` – dok je pokrenuta, tabele i podaci su u bazi; replike (8084, 8085) koriste istu tu bazu.

2. **Pokreni obe replike**  
   U IntelliJ pokreni **Backend Replica 1**, pa **Backend Replica 2**. Sačekaj da obe u konzoli prijave da su podignute (8084 i 8085).

3. **Provera da svaka replika odgovara i da koriste istu bazu**
   - U browseru ili preko **curl** / Postman:
     - **Replica 1:** `http://localhost:8084/api/cluster/demo` → u odgovoru treba `"replicaId": "replica1"`.
     - **Replica 2:** `http://localhost:8085/api/cluster/demo` → treba `"replicaId": "replica2"`.
   - Ovo potvrđuje da **logika klastera** radi (dve instance, različiti ID-ovi, obe žive).

4. **Health check**
   - `http://localhost:8084/actuator/health` i `http://localhost:8085/actuator/health` → oba treba da vraćaju `"status": "UP"` (i da pokazuju da je baza dostupna).

5. **Ista baza – opciono**  
   Npr. preko bilo kog API-ja koji piše u bazu pozovi **replicu 1** (8084), pa proveri preko **replice 2** (8085) da vidiš isti podatak (npr. lista videa, korisnika – šta već imaš). Time potvrđuješ da obe replike koriste istu bazu.

6. **Pad jedne replike (bez Nginx-a)**  
   Zaustavi u IntelliJ **Backend Replica 2**. Pozivi na `http://localhost:8085/...` neće raditi; pozivi na `http://localhost:8084/...` i dalje rade. Ponovo pokreni Replica 2 – obe opet rade. Time si potvrdila da **jedna replika može da padne, druga nastavlja da radi**.

**Rezime:** Bez Docker-a testiraš **replike + istu bazu + health + pad jedne replike**. Load balancer možeš takođe pokrenuti iz IntelliJ (vidi ispod).

---

### Testiranje Load Balancera (bez Docker-a)

Kad pokreneš **Load Balancer** (treća Run konfiguracija) zajedno sa **Backend Replica 1** i **Backend Replica 2**:

1. **Provera da load balancer živi (bez replika)**  
   Otvori u browseru: **http://localhost:8080/ping**  
   Trebalo bi da vidiš poruku: *"Load balancer radi. Za API pozive koristi npr. ..."*  
   Ako ovo ne učitava, load balancer nije pokrenut ili 8080 je zauzet.

2. **Provera round-robin (replike moraju da rade)**  
   Prvo pokreni **Backend Replica 1** i **Backend Replica 2**, pa **Load Balancer**. Zatim u browseru otvori: **http://localhost:8080/api/cluster/demo**  
   Osvježavaj stranicu (F5) više puta – u JSON odgovoru treba naizmenično da se menjaju `"replicaId": "replica1"` i `"replicaId": "replica2"`.  
   Ako vidiš "Proxy error (replike možda nisu pokrenute...)", prvo podigni obe replike i sačekaj da u konzoli piše da su na 8084 i 8085.

3. **Lista videa kroz load balancer**  
   **http://localhost:8080/api/videos** – ista lista kao na 8081/8084/8085 (replike koriste istu bazu).

---

### Testiranje sa Docker-om (ceo stack: baza + replike + Nginx)

Koristiš ovo kada hoćeš i **load balancer** i scenarije (pad replike, Nginx isključi repliku, ponovno podizanje).

1. **Pokretanje celog stack-a**
   ```bash
   cd isa-backend
   docker compose -f docker-compose.cluster.yml up -d
   ```
   Sačekaj ~1 minutu da se sve podigne (posebno da obe replike prođu health check).

2. **Jedan ulaz – Nginx (8080)**
   - U browseru ili terminalu:
     ```bash
     curl http://localhost:8080/api/cluster/demo
     ```
   - Pozovi **više puta**. U odgovoru treba naizmenično `"replicaId": "replica1"` i `"replicaId": "replica2"`. To potvrđuje **load balancer** (round-robin).

3. **Health kroz Nginx**
   ```bash
   curl http://localhost:8080/actuator/health
   ```
   Treba da dobiješ `"status": "UP"`.

4. **Pad jedne replike (Docker)**
   - Zaustavi drugu repliku:
     ```bash
     docker compose -f docker-compose.cluster.yml stop backend-replica2
     ```
   - Ponovo:
     ```bash
     curl http://localhost:8080/api/cluster/demo
     ```
   - Svi odgovori treba da dolaze od **replica1**. Nginx će nakon neuspeha prestati da šalje na replica2 (failover).

5. **Ponovno podizanje replike**
   ```bash
   docker compose -f docker-compose.cluster.yml up -d backend-replica2
   ```
   Kada replika postane healthy, ponovo pozivi na `http://localhost:8080/api/cluster/demo` naizmenično idu na obe replike.

**Rezime:** Sa Docker-om testiraš **load balancer, round-robin, health, pad i ponovno podizanje replike** – sve što zahtev traži za rad u klasteru.

---

## Pokretanje klastera (Docker)

### Preduslov

- Docker i Docker Compose na mašini.

### Jedna naredba

Iz korena `isa-backend`:

```bash
docker compose -f docker-compose.cluster.yml up -d
```

Prvo se podigne PostgreSQL, zatim dve replike aplikacije, pa Nginx. Sačekaj ~1 minutu da obe replike prođu health check.

### Provera

- **Load balancer + primer API-ja (proizvoljni poziv):**
  ```bash
  curl http://localhost:8080/api/cluster/demo
  ```
  Pozovi više puta – u odgovoru ćeš videti naizmenično `"replicaId": "replica1"` i `"replica2"` (round-robin).

- **Health (agregovano kroz Nginx):**
  ```bash
  curl http://localhost:8080/actuator/health
  ```

- **Direktno jedna replika** (bez LB):
  ```bash
  docker compose -f docker-compose.cluster.yml exec backend-replica1 wget -qO- http://localhost:8081/actuator/health
  ```

---

## Demonstracija scenarija

### 1. Pad jedne replike

```bash
docker compose -f docker-compose.cluster.yml stop backend-replica2
```

Zatim:

```bash
curl http://localhost:8080/api/cluster/demo
```

Svi odgovori dolaze sa **replica1**. Nakon otprilike 30 s Nginx prestaje da šalje zahteve na `replica2` (health check ne uspeva).

### 2. Ponovno podizanje replike

```bash
docker compose -f docker-compose.cluster.yml up -d backend-replica2
```

Kada replika postane healthy, Nginx je ponovo uključuje. Ponovo pozivi na `http://localhost:8080/api/cluster/demo` naizmenično idu na obe replike.

### 3. Parcijalni gubitak konekcije prema bazi

Možeš simulirati prekid baze za jednu repliku (npr. u `docker-compose` privremeno pogrešan `SPRING_DATASOURCE_URL` samo za `backend-replica2`). Ta replika će vratiti DOWN na `/actuator/health`, Docker healthcheck će failovati, a Nginx će je isključiti iz balansiranja; druga replika i dalje prima zahteve.

---

## Fajlovi

| Fajl | Namena |
|------|--------|
| `Dockerfile` | Build slike aplikacije (Java 17). |
| `docker-compose.cluster.yml` | PostgreSQL, dve replike (ista baza), Nginx. |
| `nginx.cluster.conf` | Upstream sa dve replike, `max_fails=3`, `fail_timeout=30s`. |
| `application-cluster.properties` | Profil za klaster (ista baza, `ddl-auto=update`). |
| `application-cluster-replica1.properties` | Replica 1 (port 8084) – za IntelliJ. |
| `application-cluster-replica2.properties` | Replica 2 (port 8085) – za IntelliJ. |
| `LocalLoadBalancer` | Load balancer na 8080 → 8084, 8085 (round-robin); pokreće se iz IntelliJ, bez Docker-a. |
| `ClusterController` (`/api/cluster/demo`) | Primer API-ja koji u odgovoru vraća `replicaId` (za dokaz rada klastera). |
| Actuator | `/actuator/health` za health check; u Security-u dozvoljen pristup bez auth. |

---

## Zaustavljanje

```bash
docker compose -f docker-compose.cluster.yml down
```

Sa brisanjem volumena baze:

```bash
docker compose -f docker-compose.cluster.yml down -v
```
