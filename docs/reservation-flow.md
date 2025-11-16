# Reserva de Parqueos – Flujo y Arquitectura

```mermaid
flowchart LR
    MapScreen -- onReserve(parkingId) --> NavHost
    NavHost -- args:userId,parkingId --> ReservationScreen
    ReservationScreen --> ReservationConfirmScreen
    ReservationConfirmScreen -- guardar --> ReservationRepository
    ReservationRepository --> ReservationLocalDataSource
    ReservationRepository --> ParkingExtrasDataSource
    ReservationListScreen <-- observe -- ReservationRepository
```

## 1. Disparo desde el mapa
- **Archivo:** `feature/map/presentation/MapScreen.kt`
- Al pulsar *Reservar* en el bottom sheet se ejecuta `onNavigateToReservation(routeInfo.destination.id)`.
- **Ruta del flujo:**
  1. `MapScreen` lanza `onNavigateToReservation`.
  2. `AppNavigation` (`navigation/AppNavigation.kt`) abre `ReservationScreen`.
  3. `ReservationScreen` (`feature/reservation/presentation/ReservationScreen.kt`) crea `ReservationViewModel`.
  4. Botón “Reservar” → `ReservationConfirmScreen` recibe fecha/horario.
  5. Tras confirmar, vuelve al mapa y desde el menú del perfil se puede abrir `ReservationListScreen`.

## 2. Navegación y menú
- **MapScreen** ahora muestra un `DropdownMenu` en el ícono de perfil:
  - *Perfil* → `ProfileScreen`.
  - *Reservas* → `ReservationListScreen`, donde se listan las reservas almacenadas localmente.
- **Nuevas rutas** en `Screen.kt`: `reservation_confirm` y `reservation_list`.

## 3. Inyección de dependencias
- `di/ReservationModule.kt` registra:
  - `ParkingExtrasDataSource` (datos estáticos del parqueo).
  - `ReservationLocalDataSource` (lista local de reservas activas).
  - `ReservationRepositoryImpl`.
  - Use cases: `GetReservationDetail`, `ConfirmReservation`, `ObserveActiveReservations`.
  - ViewModels: `ReservationViewModel`, `ReservationConfirmViewModel`, `ReservationListViewModel`.

## 4. Capas de datos/dominio
- `ReservationRepositoryImpl` combina:
  - `IParkingRepository` → datos base del parqueo.
  - `ParkingExtrasDataSource` → foto/amenities (`imageName` apunta a drawables locales).
  - `ReservationLocalDataSource` → guarda/observa `ReservationRecordDto`.
- Modelos nuevos:
  - `ReservationRequestModel` (petición al confirmar).
  - `ReservationRecordModel` + `ReservationStatus` (registro persistido).
- `ObserveActiveReservationsUseCase` expone un `Flow` filtrado por `userId`.

## 5. ViewModels
- `ReservationViewModel`: mantiene estado del formulario (fecha, horas, costo).
- `ReservationConfirmViewModel`: carga detalle del parqueo y ejecuta `ConfirmReservationUseCase`.
- `ReservationListViewModel`: observa reservas activas para la pantalla de historial.

## 6. UI (Jetpack Compose)
- `ReservationScreen`: formulario inicial + selector de fecha/horarios.
- `ReservationConfirmScreen`: muestra resumen y botón “Confirmar”.
- `ReservationListScreen`: lista simple de reservas activas.
- `ReservationBottomBar` navega a la confirmación solo si no hay errores de validación.

## 7. TODOs / próximos pasos
1. Conectar `ReservationConfirmViewModel` con backend real.
2. Sincronizar `ReservationLocalDataSource` con almacenamiento persistente (Room/Firestore).
3. Añadir flujo de cancelación/completado y estados adicionales.
4. Notificar al usuario (snackbar/Push) al confirmar la reserva.

### Referencia rápida

| Capa | Archivo | Descripción |
| --- | --- | --- |
| Map Feature | `feature/map/presentation/MapScreen.kt` | Botón reservar + menú Perfil/Reservas |
| Navegación | `navigation/AppNavigation.kt` | Rutas de formulario, confirmación y lista |
| Datos | `feature/reservation/data/...` | Datasources (extras + local), repositorio |
| Dominio | `feature/reservation/domain/...` | Modelos y use cases |
| Presentación | `feature/reservation/presentation/...` | ViewModels y pantallas Compose |

### Personalizar imágenes de parqueos
1. Copia tus imágenes (`.png/.jpg/.webp`) dentro de `app/src/main/res/drawable`.
2. En `ParkingExtrasDataSource.kt` coloca el nombre (sin extensión) en `imageName`.
3. La UI resolverá el recurso automáticamente; si no existe, usa `img_parking_default`.

### Nuevo menú en el mapa
El `DropdownMenu` del ícono de perfil permite saltar rápidamente a:
1. **Perfil** → `ProfileScreen`.
2. **Reservas** → `ReservationListScreen` (historial activo almacenado localmente).
