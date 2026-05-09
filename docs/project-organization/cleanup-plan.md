# Cleanup Plan

## Purpose

This plan describes future clean-up actions only. It does not perform any file movement, deletion, or code refactoring.

## safe

These items can generally be organized with low risk after manual confirmation:

- add more index documents under `docs/project-organization/`
- add read-only documentation for accepted interface behavior
- add README files for documentation folders
- add SQL execution notes
- add API import notes

## caution

These items require human confirmation before any action:

- README content updates
- frontend page path renaming
- SQL file relocation
- API export format changes
- any change to accepted endpoint examples
- any update to backend module naming conventions

## forbidden

These items must not be automatically organized:

- `node_modules`
- `target`
- `dist`
- `.git`
- `.idea`
- `.vscode`
- `build`
- `out`
- `coverage`
- `logs`
- deleting verified real-device modules
- moving validated SQL files
- changing the verified OpenAPI JSON
- modifying backend or frontend business code
- modifying `application.yml`

## Explicit Rules

- Do not delete the verified real-device modules.
- Do not move the validated SQL files.
- Do not modify the validated OpenAPI JSON.
- Do not touch README files unless manually confirmed later.
- Do not touch excluded directories.

## Recommended Next Steps

1. Keep the current delivery frozen.
2. Use this index as the single source of truth for manual organization review.
3. Decide later whether any physical directory cleanup is necessary.

