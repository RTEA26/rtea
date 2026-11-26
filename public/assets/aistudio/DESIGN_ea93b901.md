---
name: Serene Spectrum
colors:
  surface: '#fcf9f4'
  surface-dim: '#dcdad5'
  surface-bright: '#fcf9f4'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f6f3ee'
  surface-container: '#f0ede9'
  surface-container-high: '#ebe8e3'
  surface-container-highest: '#e5e2dd'
  on-surface: '#1c1c19'
  on-surface-variant: '#41474e'
  inverse-surface: '#31302d'
  inverse-on-surface: '#f3f0eb'
  outline: '#72787f'
  outline-variant: '#c1c7d0'
  surface-tint: '#2c628e'
  primary: '#2c628e'
  on-primary: '#ffffff'
  primary-container: '#76a8d8'
  on-primary-container: '#003d62'
  inverse-primary: '#99cbfd'
  secondary: '#8e4d2b'
  on-secondary: '#ffffff'
  secondary-container: '#ffaa81'
  on-secondary-container: '#793c1b'
  tertiary: '#226a57'
  on-tertiary: '#ffffff'
  tertiary-container: '#6db19b'
  on-tertiary-container: '#004334'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#cee5ff'
  primary-fixed-dim: '#99cbfd'
  on-primary-fixed: '#001d33'
  on-primary-fixed-variant: '#074a75'
  secondary-fixed: '#ffdbcc'
  secondary-fixed-dim: '#ffb693'
  on-secondary-fixed: '#351000'
  on-secondary-fixed-variant: '#713616'
  tertiary-fixed: '#aaf1d9'
  tertiary-fixed-dim: '#8fd4bd'
  on-tertiary-fixed: '#002018'
  on-tertiary-fixed-variant: '#005140'
  background: '#fcf9f4'
  on-background: '#1c1c19'
  surface-variant: '#e5e2dd'
typography:
  display-lg:
    fontFamily: Nunito Sans
    fontSize: 40px
    fontWeight: '700'
    lineHeight: 52px
    letterSpacing: -0.01em
  display-lg-mobile:
    fontFamily: Nunito Sans
    fontSize: 30px
    fontWeight: '700'
    lineHeight: 40px
  headline-lg:
    fontFamily: Nunito Sans
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 38px
  headline-md:
    fontFamily: Nunito Sans
    fontSize: 22px
    fontWeight: '600'
    lineHeight: 30px
  headline-sm:
    fontFamily: Nunito Sans
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 26px
  body-lg:
    fontFamily: Nunito Sans
    fontSize: 17px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Nunito Sans
    fontSize: 15px
    fontWeight: '400'
    lineHeight: 24px
  body-sm:
    fontFamily: Nunito Sans
    fontSize: 13px
    fontWeight: '400'
    lineHeight: 20px
  label-lg:
    fontFamily: Nunito Sans
    fontSize: 15px
    fontWeight: '600'
    lineHeight: 22px
  label-md:
    fontFamily: Nunito Sans
    fontSize: 13px
    fontWeight: '600'
    lineHeight: 18px
  label-sm:
    fontFamily: Nunito Sans
    fontSize: 11px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.02em
rounded:
  sm: 0.5rem
  DEFAULT: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  full: 9999px
spacing:
  gutter: 1rem
  gutter-lg: 1.5rem
  margin: 1.25rem
  margin-md: 2rem
  margin-lg: 3rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2.5rem
---

## Brand & Style

This design system is built specifically for neuroinclusive, sensory-friendly digital environments, drawing chromatic inspiration from the vibrant R-TEA identity (orange/coral, sky blue, yellow, mint, and rainbow infinity violet) while intentionally softening saturation into comforting, low-arousal pastel hues. 

The primary goal is minimizing cognitive friction, hyper-stimulation, and visual stress for autistic and neurodivergent individuals, their families, and specialists. The emotional tone is reassuring, warm, predictable, and gentle.

The aesthetic blends **Soft Minimalist Warmth** with organic, approachable geometry:
- Crisp white and pure black are completely avoided to eliminate harsh glare and visual fatigue; interfaces sit on creamy ivory, warm eggshell, and soft off-white canvas tones.
- High-contrast visual jolts are replaced with soft tonal layering, generous breathing room, and pill-shaped touch targets that feel inviting rather than mechanical.
- Accents from the multi-colored infinity emblem are used contextually for wayfinding and categorization rather than decorative saturation.

## Colors

The palette derives directly from the logo elements, converted to calibrated pastels that prevent sensory overload while maintaining WCAG AA contrast for text accessibility.

### Palette Roles
- **Primary (`#76A8D8` - Pastel Sky Blue):** Derived from the vibrant blue lettering and swirl; establishes calm, clarity, and trust. Used for primary interactions, selected states, and navigational highlights.
- **Secondary (`#F5A178` - Soft Coral Sorbet):** Derived from the warm orange 'R' and puzzle piece; provides warmth, positive reinforcement, and active interactive accents without inducing urgency or anxiety.
- **Tertiary (`#89CEB7` - Tranquil Mint):** Inspired by the green transitions in the infinity loop; represents growth, progress, and confirmation states.
- **Neutral Canvas (`#FAF7F2` - Warm Ivory / Off-White):** A matte, warm paper-like background that eradicates high-luminance blue glare.
- **Supportive Accents:**
  - *Calm Buttercup (`#FDE68A` / `#F7D070`):* Soft yellow for highlights and informative cards.
  - *Lavender Mist (`#B8A7E0`):* Softened violet from the infinity spectrum, ideal for reflective notes, parent/carer insights, and quiet tags.
  - *Text Slate (`#2E3842`):* Deep desaturated slate for primary body text, offering high contrast without the stark harshness of `#000000`.

## Typography

Typography relies uniformly on **Nunito Sans**, chosen for its humanist proportions, rounded terminal junctions, open counters, and high legibility. These attributes eliminate visual tension and provide a comforting, dyslexia-friendly cadence.

- Generous line-heights across body copy ensure that lines do not crowd each other, preventing crowding stress.
- Letter spacing remains natural to slightly loose, prioritizing character distinction.
- Hierarchy is achieved primarily through font weight (`600` vs `400`) and subtle scale steps rather than aggressive size jumps.

## Layout & Spacing

The layout philosophy uses a structured fluid grid paired with generous negative space. Interfaces avoid high-density data clutter; each section is isolated with distinct margins to allow the eye to rest.

- **Breakpoints:**
  - *Mobile (<640px):* 4-column layout, `margin: 1.25rem`, single-column stacked containers.
  - *Tablet (640px–1024px):* 8-column layout, `margin: 2rem`, 2-column card groupings.
  - *Desktop (>1024px):* 12-column layout capped at max 1200px width, `margin: 3rem`, prioritizing centered reading columns to avoid neck strain and eye drift.
- **Rhythm:** Spacing follows an 8px base rhythm (`0.5rem`, `1rem`, `1.5rem`, `2.5rem`). Whitespace is deliberate and serves as functional sensory buffering.

## Elevation & Depth

To avoid optical harshness, traditional hard drop shadows and heavy multi-layered 3D perspectives are excluded. Depth is achieved via **Tonal Stacking** and **Subtle Ambient Halos**:

- **Surface Tiers:**
  - *Ground (Canvas):* `#FAF7F2` (Warm Ivory).
  - *Card & Container Surfaces:* `#FFFFFF` (Softened pure surface) or `#F3EFE9` (Warm tint surface).
  - *Raised Floating Surfaces (Modals, Overlays):* Tinted ambient shadow: `0 8px 24px -4px rgba(46, 56, 66, 0.06), 0 2px 6px -1px rgba(46, 56, 66, 0.04)`.
- **Borders:** Containers use subtle, warm hair-line borders (`1px solid #ECE6DC`) that clearly demarcate boundaries without adding sharp visual weight.

## Shapes

The shape system adopts a pill-shaped and deeply rounded profile (`level 3`). Sharp angles evoke rigidity and tension, whereas soft radii and organic pill shapes convey safety, tactile comfort, and warmth.

- Interactive elements (buttons, chips, badges) leverage full pill radiuses (`rounded-full` / `9999px`).
- Structural containers and cards utilize `1.5rem` to `2rem` border radiuses.
- Input fields and smaller grouped modules utilize `1rem` corner rounding.

## Components

### Buttons
- **Primary:** Background in Pastel Sky Blue (`#76A8D8`) with Slate White text (`#FFFFFF`), pill-shaped (`9999px` radius), with `padding: 0.75rem 1.75rem`. Hover state subtly deepens tone without jarring motion (`#6799C9`).
- **Secondary / Warm Action:** Soft Coral (`#F5A178`) background, white text. Reserved for affirmative or playful milestones.
- **Ghost / Tertiary:** Transparent background, `1.5px` border in `#D4CCBF`, text in `#2E3842`. Soft ivory wash on hover (`#F0EBE3`).

### Chips & Tags
- Pill-shaped capsules with generous vertical padding (`0.4rem 1rem`).
- Thematic category chips make direct use of the pastel palette:
  - *Blue Chip:* `#E8F1FA` background with `#4B7DAE` text.
  - *Coral Chip:* `#FDEEE7` background with `#BA673F` text.
  - *Mint Chip:* `#E9F6F1` background with `#478A73` text.
  - *Lavender Chip:* `#F1EDFA` background with `#735FA3` text.

### Cards & Content Containers
- Clean off-white surface (`#FFFFFF`) with a `1.5rem` corner radius and a faint border (`1px solid #EFEAE1`).
- Ample inner padding (`1.5rem` to `2rem`).
- Cards avoid complex hover animations; hover indicators use gentle elevation changes or warm border tint transitions.

### Input Fields
- Generously sized with `3rem` target height and `1rem` rounded corners.
- Background in `#F8F5EE` with `1px` border in `#E2D9CC`.
- Focus state softens the ring: a smooth 3px outer glow in pastel blue (`rgba(118, 168, 216, 0.35)`) and border `#76A8D8`, avoiding sudden jarring shifts.

### Checkboxes & Radio Buttons
- Enlarged touch targets (minimum 24px) with rounded contours (radios full-circle, checkboxes with 8px radius).
- Check fills utilize Tranquil Mint (`#89CEB7`) or Pastel Sky Blue (`#76A8D8`) with soft check marks, reducing micro-frustration.

### Visual Anchors & Indicators
- Friendly iconography with thick, rounded stroke terminals matching the Nunito geometry.
- Progress bars use smooth, pastel rainbow progression inspired by the infinity loop, filling gradually without fast flickering flashes.