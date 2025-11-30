# Resumen de pruebas (unidad / integración / UI)

Guía rápida de qué se prueba, en qué carpeta del feature impacta cada prueba y el tipo de prueba.

## Auth
- `app/src/test/java/com/miskidev/miskiparqueo/feature/auth/login/domain/usecases/LoginUseCaseTest.kt` — Unit. Cubre `feature/auth/login/domain` (use case) y su contrato con `feature/auth/login/domain/repository`.
- `app/src/test/java/com/miskidev/miskiparqueo/feature/auth/login/presentation/LoginViewModelTest.kt` — Unit. Cubre `feature/auth/login/presentation` (estado de UI) usando fakes de `feature/auth/login/domain`.
- `app/src/test/java/com/miskidev/miskiparqueo/feature/auth/signup/domain/model/vo/SignUpValueObjectsTest.kt` — Unit. Cubre `feature/auth/signup/domain/model/vo` (validaciones de Email/Password/Username/FirstName/LastName).
- `app/src/test/java/com/miskidev/miskiparqueo/feature/auth/signup/presentation/SignUpViewModelTest.kt` — Unit. Cubre `feature/auth/signup/presentation` (estado de registro) y usa `feature/auth/signup/domain/usecases`.
- `app/src/test/java/com/miskidev/miskiparqueo/feature/auth/ui/AuthScreensUiTest.kt` — UI (Robolectric + Compose). Cubre `feature/auth/login/presentation` y `feature/auth/signup/presentation` comprobando interacciones de pantalla y navegación.

## Maintenance
- `app/src/test/java/com/miskidev/miskiparqueo/feature/maintenance/data/MaintenanceDataStoreTest.kt` — Integration (Robolectric). Cubre `feature/maintenance/data` (DataStore de preferencias).
- `app/src/test/java/com/miskidev/miskiparqueo/feature/maintenance/data/MaintenanceRepositoryTest.kt` — Unit. Cubre `feature/maintenance/data` (Remote Config + datastore, incl. fallback).
- `app/src/test/java/com/miskidev/miskiparqueo/feature/maintenance/presentation/MaintenanceViewModelTest.kt` — Unit. Cubre `feature/maintenance/presentation` (state flow y refresh) con mocks del repo.

## Map
- `app/src/test/java/com/miskidev/miskiparqueo/feature/map/data/ParkingRepositoryImplTest.kt` — Unit. Cubre `feature/map/data/repository` (mapeo DTO→dominio, error si falta parking).
- `app/src/test/java/com/miskidev/miskiparqueo/feature/map/domain/usecases/CalculateRouteUseCaseTest.kt` — Unit. Cubre `feature/map/domain/usecases` y `feature/map/domain/model` (cálculo distancia/tiempo).
- `app/src/test/java/com/miskidev/miskiparqueo/feature/map/domain/usecases/SearchParkingsUseCaseTest.kt` — Unit. Cubre `feature/map/domain/usecases` sobre `feature/map/domain/repository`.
- `app/src/test/java/com/miskidev/miskiparqueo/feature/map/presentation/MapViewModelTest.kt` — Unit. Cubre `feature/map/presentation` (estados de mapa, búsqueda, rutas) usando fakes de `feature/map/domain`.

## Profile
- `app/src/test/java/com/miskidev/miskiparqueo/feature/profile/domain/ProfileUseCasesTest.kt` — Unit. Cubre `feature/profile/domain/usecases` delegando al repo.
- `app/src/test/java/com/miskidev/miskiparqueo/feature/profile/data/ProfileRepositoryImplTest.kt` — Unit. Cubre `feature/profile/data/repository` delegando al datasource Firebase.
- `app/src/test/java/com/miskidev/miskiparqueo/feature/profile/presentation/ProfileViewModelTest.kt` — Unit. Cubre `feature/profile/presentation/profile` (carga de usuario, ediciones, guardado).
- `app/src/test/java/com/miskidev/miskiparqueo/feature/profile/presentation/ChangePasswordViewModelTest.kt` — Unit. Cubre `feature/profile/presentation/changepassword` (validaciones y estados) usando `feature/profile/domain/usecases`.

## Reservation
- `app/src/test/java/com/miskidev/miskiparqueo/feature/reservation/domain/ReservationUseCasesTest.kt` — Unit. Cubre `feature/reservation/domain/usecases` y `feature/reservation/domain/model` delegando al repo.
- `app/src/test/java/com/miskidev/miskiparqueo/feature/reservation/presentation/ReservationViewModelTest.kt` — Unit. Cubre `feature/reservation/presentation` (carga, validaciones de horario, costo, errores).
- `app/src/test/java/com/miskidev/miskiparqueo/feature/reservation/ui/ReservationScreenUiTest.kt` — UI (Robolectric + Compose). Cubre `feature/reservation/presentation` para render de pantallas y estados con cupos.

## Otros / Compartidos
- `app/src/test/java/com/miskidev/miskiparqueo/ExampleUnitTest.kt` — Unit. Prueba genérica (suma) sin impactar features.
- `app/src/test/java/com/miskidev/miskiparqueo/MainDispatcherRule.kt` — Regla de test (no es prueba, pero usada en tests de presentation de varios features).
