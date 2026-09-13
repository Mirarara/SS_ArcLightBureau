# Arc Light Bureau

English translation of Arc Light Bureau 1.12.3 for Starsector 0.98a-RC8.
Arc Light Bureau is the new name of Traverser Design Bureau mod.

## Requirements

- LazyLib
- MagicLib
- GraphicsLib

## Installation

Copy the `ArcLightBureau` folder into Starsector's `mods` folder and enable **Arc Light Bureau** in the launcher.

## Source and build

The translated Java source is retained under `jars/src`. From PowerShell, rebuild the mod with:

```powershell
.\build.ps1 -StarsectorPath "<path to Starsector>"
```

The script uses Starsector's bundled JDK, compiles all Java sources for Java 17, and rebuilds and verifies `jars\TraverserDesignBureau3.jar`. It automatically locates LazyLib, MagicLib, GraphicsLib, BoxUtil, and LunaLib under Starsector's `mods` folder. LunaLib is needed only to compile against current GraphicsLib; it is not an additional runtime requirement for Arc Light Bureau. Use the corresponding `-...Jar` parameters or `-JdkPath` for non-standard installations.

## Credits

- Design and production: 寒流
- Balance and loadout guidance: Scythe
- Technical guidance: AnyIDElse
- Artwork and art guidance: 影之光, 神枪, and 狐狸条
- Special thanks: 烤乳猪, Greatfhgbj, cjy4312, Ykaris, homejerry99, CVB～Taiho.0, 鸽子, 幻音, and the QQ group contributors
- English translation: Myco, Miko, and Mirarara
