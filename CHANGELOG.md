All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [0.2.0]
### Added
- floor level, floor certainty, altitude and bearing on location change
- quality when calibration changes

## [0.1.0] 2019-01-06
### Added
- exposed location service status codes and calibration codes constants
- regionEnter event, with a lot of floor map data
- regionExit event

### Changed
- internal code cleanup (easier to add new events)
- updated IndoorAtlas Android module to 2.9.0

### Removed
- returning region name on `locationChanged`

## [0.0.4] 2018-12-07
### Fixed
- crash when Indoor returns null region

## [0.0.3] 2018-11-22
### Added
- events data description in docs

## [0.0.2] 2018-11-22
### Added
- proper README
- some tests
- some jsdocs
- usage example docs

## [0.0.1] 2018-11-21
### Added
- first working version
