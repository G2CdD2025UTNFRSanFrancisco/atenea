# Configuración CORS en Atenea

## Overview

Se ha implementado una configuración CORS (Cross-Origin Resource Sharing) flexible que permite:

- **En Development**: Permitir todos los orígenes (*)
- **En Production**: Restringir a dominios específicos

## Archivos Creados/Modificados

1. **AteneaCorsConfiguration.java** - Nueva clase de configuración CORS
   - Ubicación: `src/main/java/ar/edu/utn/sanfrancisco/atenea/infrastructure/AteneaCorsConfiguration.java`
   - Configurable mediante properties

2. **AteneaConfiguration.java** - Actualizado
   - Agregado import de `CorsConfigurationSource`
   - Modificado método `apiChain()` para incluir CORS

3. **application.properties** - Actualizado
   - Agregadas propiedades de configuración CORS por defecto

4. **application-dev.properties** - Nuevo
   - Configuración CORS para desarrollo (permite todo)

5. **application-prod.properties** - Nuevo
   - Configuración CORS para producción (solo dominios específicos)

## Propiedades de Configuración

Las siguientes propiedades pueden personalizarse en los archivos de configuración:

```properties
# Orígenes permitidos (separados por coma)
atenea.cors.allowed-origins=http://localhost:3000,http://localhost:4200

# Métodos HTTP permitidos
atenea.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS,PATCH

# Headers permitidos (* permite todos)
atenea.cors.allowed-headers=*

# Tiempo máximo de caché (en segundos)
atenea.cors.max-age=3600

# Permitir credenciales
atenea.cors.allow-credentials=true
```

## Uso

### Development (por defecto)

Para ejecutar en modo desarrollo:

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

O simplemente:

```bash
./gradlew bootRun
```

**Comportamiento**: Permite CORS desde cualquier origen (*)

### Production

Para ejecutar en modo producción:

```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```

**Comportamiento**: Solo permite los dominios configurados en `application-prod.properties`

## Configuración por Perfil

### application.properties (Defecto)
```properties
atenea.cors.allowed-origins=http://localhost:3000
atenea.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS,PATCH
atenea.cors.allowed-headers=*
atenea.cors.max-age=3600
atenea.cors.allow-credentials=true
```

### application-dev.properties (Development)
```properties
spring.profiles.active=dev

# Permite todo
atenea.cors.allowed-origins=*
atenea.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS,PATCH
atenea.cors.allowed-headers=*
atenea.cors.max-age=3600
atenea.cors.allow-credentials=true
```

### application-prod.properties (Production)
```properties
# Solo dominios específicos
atenea.cors.allowed-origins=https://example.com,https://app.example.com
atenea.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS,PATCH
atenea.cors.allowed-headers=Authorization,Content-Type,Accept
atenea.cors.max-age=7200
atenea.cors.allow-credentials=true
```

## Personalización

Para usar dominios personalizados, edita `application-prod.properties`:

```properties
atenea.cors.allowed-origins=https://tu-dominio.com,https://api.tu-dominio.com,https://app.tu-dominio.com
```

## Endpoints Disponibles

Todos los endpoints están configurados con CORS:

- `/api/v1/sessions` (Login)
- `/api/v1/sessions/mfa` (MFA)
- `/api/v1/accounts/**` (Gestión de cuentas)
- `/api/v1/spots/**` (Gestión de estacionamientos)
- `/ws/**` (WebSocket)
- `/websocket-client.html` (Cliente WebSocket)

## Notas Importantes

- En **desarrollo**, el wildcard `*` permite CORS desde cualquier origen para facilitar el testing
- En **producción**, es recomendable especificar explícitamente los dominios permitidos
- Los headers `Authorization`, `Content-Type` y `Accept` son los mínimos recomendados para production
- El tiempo de caché (`max-age`) es mayor en production (7200s) que en development (3600s)

