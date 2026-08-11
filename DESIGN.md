# DMZ: Generations — Diseño del Sistema de Edades y Generaciones

> Documento de diseño (la "biblia" del addon). Addon para **DragonMineZ** (Forge 1.20.1).
> Owner: **Yuseix300** · Mod id: `dmzgenerations`
>
> Todos los números son **valores por defecto** y **editables por config**.

---

## 1. Concepto

Tu personaje **envejece** con el paso de los días de Minecraft. De niño eres débil pero
rápido y con mucho potencial; de adulto alcanzas tu pico de poder; de anciano decaes
físicamente pero ganas maestría... y los achaques te acompañan para siempre. En cualquier
momento, siendo viejo, puedes **renacer** (reencarnar) como cualquier raza, conservando
solo un **buff de poder acumulado**. Cada vida hace tu linaje un poco más fuerte.

Inspiración: el sistema de edad de **Dragon Block C** (mensaje diario de crecimiento +
modelo que escala con la edad), reinterpretado.

---

## 2. Envejecimiento

- **Reloj = el día de Minecraft.** Cada amanecer envejeces y sale un aviso:
  `☀ Nuevo día — Has crecido. Ahora tienes X años.`
- **Solo se envejece estando conectado.** Nada de envejecer offline/AFK.
  (Se cuenta el tiempo/días jugados por jugador.)
- **Dormir cuenta:** saltarte la noche avanza el día → creces. Le da peso a dormir.
- **Goteo de poder:** cada día ganas un poquito de stats base (*crecer = más fuerte*).
- **Único acelerador: la Habitación del Tiempo (HTC).** Mientras el jugador está en la
  dimensión HTC (`HTCDimension`), el envejecimiento se multiplica (`htcAgingMultiplier`,
  ~×15–20). Entrenar fuera **no** te envejece. Tradeoff clásico DB: poder rápido a cambio
  de salir más viejo.
- **Igual para todas las razas, incluidas las custom de addons.** Un único `añosPorDia`
  y umbrales globales; las razas custom heredan los valores globales sin configurar nada.

**Ritmo por defecto:** `añosPorDia = 4` (fuera de HTC).

---

## 3. Etapas de vida

| Etapa | Rango de edad* | Mult. stats base | Ganancia entrenamiento | Velocidad |
|---|---|---|---|---|
| Niño | 0–15 | ×0.55 | ×1.6 (alto potencial) | **más rápido** |
| Adolescente | 15–25 | ×0.75 | ×1.35 | rápido |
| Joven adulto | 25–40 | ×0.95 | ×1.1 | media |
| **Adulto / prime** | 40–95 | ×1.0 (pico) | ×1.0 | media-baja |
| Anciano | 95+ (permanente) | ×0.8 y bajando | ×0.85 | **más lento** |

\*Rangos por defecto, editables. La edad **sigue subiendo para siempre** (no hay tope).

- **Relación clave (potencial vs. poder bruto):** de joven tienes stats bajos pero
  **ganas entrenamiento más rápido**; de adulto, pico de poder bruto. Recompensa vivir
  toda la vida en vez de correr a una etapa.
- **Velocidad de movimiento inversa a la edad:** niño = ágil/veloz (pega con el cuerpo
  chibi), adulto = más lento, anciano = el más lento. Es su propia curva.
- **Estirón:** al cambiar de etapa → **sonido + partículas + subidón único de stats**.

**Edad inicial (creación de personaje):** por defecto **niño**, pero el jugador **elige**
entre niño / adolescente / joven / adulto / anciano.
- Empezar mayor = más fuerte ya, pero te pierdes la velocidad y las ganancias de niño.
- Empezar de anciano = reto con achaques desde el minuto uno.

---

## 4. La vejez (permanente)

- **No se muere de viejo.** Anciano es un estado permanente.
- **Achaques (infartos):**
  - Te quitan **poco daño** (~1–2 corazones). **Nunca letales**: no pueden bajarte de
    **½ corazón** (nunca dan el golpe final).
  - **Más frecuentes cuanto más viejo**, hasta una **frecuencia máxima con tope** (para que
    "viejo para siempre" siga siendo jugable):
    | Sub-etapa | Frecuencia aprox. |
    |---|---|
    | 95–105 | cada ~8 min |
    | 105–115 | cada ~4 min |
    | 115+ | cada ~1 min (tope) |
  - **Efecto:** pinchazo de daño + toque visual breve (viñeta roja ~1s) + sonido de latido.
- **Maestría de anciano (compensación):** **coste reducido de ki/técnicas** y mejor control
  de ki. Ser viejo es una elección válida (máximo poder en un cuerpo experimentado), no un
  castigo puro.

---

## 5. Renacer y buff generacional

- **Voluntario, vía NPC.** Siendo anciano, hablas con **Enma / King Yemma** (supervisor de
  almas en el Otro Mundo — el que mejor encaja con reencarnar) y/o **Dende** (ya tiene un
  botón "Reset Stats" en su menú; añadimos "Renacer" al lado).
- **Reencarnación total, NO dinastía familiar:** al renacer vuelves a **creación de
  personaje** y **eliges cualquier raza** (puedes cambiar de raza cada generación), nuevo
  aspecto y nombre.
- **Lo único que se conserva es el buff.** *Empiezas solo con el buffo.* Nada de heredar
  técnicas ni rasgos.
- **El buff = % permanente sobre TODAS las stats**, neutro de raza (funciona con cualquier
  raza que elijas). **Se acumula cada renacer** con **rendimientos decrecientes y tope**.
  - Ejemplo: Gen 1 = +0% · Gen 2 = +12% · Gen 3 = +21% · Gen 4 = +27% (acercándose al tope).
- Renacer te devuelve joven → **se van los achaques**.
- **Tensión de diseño:** quedarte anciano eterno (máximo poder, pero frágil y con achaques)
  **vs.** renacer (cuerpo fresco y sano, linaje más fuerte). Ambas válidas.

---

## 6. Muerte y Otro Mundo

- **Morir en combate** te deja con **la edad que tenías** (no se pierde). Respawn normal.
- **No envejeces mientras estás muerto** — con el **halo puesto / en el Otro Mundo** el reloj
  de edad se **pausa** hasta que revivas y vuelvas a la Tierra.
- Lo **único** que resetea el personaje es el renacer voluntario con Enma/Dende.

---

## 7. Visual

### 7.1 Escalado del modelo (chibi cabezón)
- Encoger **cuerpo, brazos y piernas** (y sus bones hijos: manos, pies) pero **mantener la
  cabeza a tamaño normal** → efecto cabezón entrañable.
- **La hitbox baja también** (sincronizada con la edad), vía Forge `EntityEvent.Size`.
- Tech: DMZ usa **GeckoLib** → se escalan los *bones*. Como la cabeza suele colgar del bone
  del cuerpo, hay que **contra-escalar la cabeza** (o sacarla de la jerarquía) para que quede
  a tamaño real mientras el resto mengua.

### 7.2 Composición con transformaciones (¡importante!)
Algunas formas ya escalan al jugador (ej. **SSJ Grade 3 = 1.3**). Se combinan
**multiplicativamente**:

> **escala final = escala por edad × escala de la forma**

| Estado | Edad | × Forma | = Final | Resultado |
|---|---|---|---|---|
| Adulto en SSJ3 | 1.0 | ×1.3 | 1.3 | igual que hoy (no cambia) |
| Niño en SSJ3 | 0.6 | ×1.3 | 0.78 | **sigue siendo niño, solo un poco más grande** |

- El niño se mantiene niño al transformarse, solo **aumenta un poco** (proporcional).
- El adulto **no cambia** respecto a como se ve hoy (su escala de edad es 1.0).
- La cabeza sigue grandota (chibi) porque sigue siendo niño.
- **EXCEPCIÓN — Oozaru** 🦍: queda **fuera** de todo el sistema de escalado por edad. Es un
  modelo/forma gigante aparte; siempre se renderiza a su **tamaño completo** sin importar la
  edad (sin encoger, sin preservar cabeza).

### 7.3 Pelo que envejece
- Con la edad, el pelo **se pone gris/blanco**; de anciano salen **canas/barba**. Los niños
  **no** pueden tener barba. Se apoya en el sistema de pelo existente (`HairEditorScreen`).

---

## 8. Social (multijugador)

- **Anciano como maestro:** un jugador **anciano** cerca de jugadores **más jóvenes**
  (niño/adolescente) les da un **boost pasivo a sus ganancias de entrenamiento**
  (~+25% dentro de X bloques; radio y % por config).
  - No da stats gratis: el joven igual tiene que entrenar, solo rinde más.
  - Le da propósito al anciano débil-pero-sabio.
  - Brilla en servers; en solitario casi no aplica (no puedes ser viejo y niño a la vez).

---

## 9. Deseos (wishes)

- **"Restaurar Juventud"** (deseo de DMZ): **reduce la edad** del jugador **sin renacer** —
  mantienes raza y progreso, solo rejuveneces y **se van los achaques**.
- (Opcional/flavor futuro: deseo de "juventud eterna" que congela el envejecimiento.)

---

## 10. UI / Display de edad

Se muestra en **3 sitios**:
1. **HUD** — siempre visible, discreto.
2. **Menú de stats** — con etapa de vida.
3. **Tooltip al pasar el cursor sobre el nombre** en el menú de stats — muestra todo junto:
   **edad + etapa + generación (insignia) + % de buff**.

---

## 11. Comandos de admin

Imprescindibles para testear (no esperar días reales) y para admins de servers:
- `/dmzgen age set|get <jugador> [valor]`
- `/dmzgen stage set <jugador> <etapa>`
- `/dmzgen generation set|get <jugador> [valor]`
- `/dmzgen buff set|get <jugador> [valor]`
- `/dmzgen rebirth <jugador>` (forzar renacer)
- *(nombres provisionales)*

---

## 12. Config

- **Presets de ritmo:** `rápido / normal / lento / hardcore` para el envejecimiento, para no
  calcular números a mano.
- **Knobs individuales** (todo editable): `añosPorDia`, `htcAgingMultiplier`, umbrales de
  etapa, curva de stats por etapa, curva de velocidad, `heartAttacksEnabled`, daño y
  frecuencia de achaques + tope, `elderMasteryFactor`, `buffPorGeneracion`, `buffTope`,
  radio/% del maestro anciano, etc.
- **Descartado:** ítem "medicina para el corazón" (no se implementa).

---

## 13. Puntos de integración técnica (referencia para implementar)

- **Estado del jugador:** nuevo componente `Age` dentro de `StatsData` (edad en años/ticks,
  etapa actual, generación, buff acumulado). Cuidar login / clone / death / dimension-change /
  tick / **sync** / save-load. Sincronizar al cliente (necesario para HUD y render).
- **Reloj:** tick del servidor que, al cambiar el día de MC, avanza la edad de los jugadores
  online; pausar si tienen halo / están en el Otro Mundo; multiplicar si están en HTC.
- **HTC:** detectar `player.level().dimension() == HTCDimension key`.
- **NPCs:** menús de **Enma** y **Dende** → opción "Renacer" (vía `NPCActionC2S`), gated a
  etapa anciano.
- **Render:** escalado de bones con **GeckoLib** (edad × forma, Oozaru excluido);
  contra-escalado de la cabeza.
- **Hitbox:** Forge `EntityEvent.Size` sincronizado con la edad.
- **Pelo:** enganchar con el sistema de pelo / `HairEditorScreen`.
- **Networking:** registrar los packets nuevos en el canal del addon con dirección explícita;
  **validar en servidor** (nunca confiar en el cliente para edad, buff, generación).
- **Config:** clases de config con `CURRENT_VERSION`; sync servidor→cliente; recarga.

---

## 14. Descartado (para que quede constancia)

- Muerte natural por vejez (la vejez es permanente; solo renacer resetea).
- Achaques letales.
- Envejecer offline.
- Dinastía familiar / heredar raza o técnicas al renacer (solo el buff).
- Libro de vidas pasadas.
- Aura gigante chibi como chiste.
- Perk de "agilidad de niño" y "tinte de aura por generación" (no tomados).
- Ítem medicina para el corazón.

---

## 15. Orden de implementación sugerido (fases)

1. **Núcleo:** componente `Age` en `StatsData` + tick diario + mensaje "has crecido" + sync +
   comandos de admin (para poder testear ya).
2. **Etapas y stats:** umbrales, curva de stats, velocidad, estirón (sonido/partículas).
3. **Vejez:** achaques (no letales, escalado + tope) + maestría de anciano.
4. **Renacer + buff:** menús Enma/Dende, creación de personaje, buff % acumulado.
5. **Visual:** escalado chibi del modelo + hitbox + composición con formas (Oozaru excepto).
6. **Pelo que envejece.**
7. **Reglas de muerte/Otro Mundo** (pausa de edad con halo).
8. **Maestro anciano** (multijugador).
9. **Deseo "Restaurar Juventud".**
10. **UI/Display** (HUD + stats + tooltip) — se puede ir haciendo en paralelo.
11. **Config** (presets + knobs).
