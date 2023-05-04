/** Author: Tina DiLorenzo */

/** @TODO */
// 1. CREATE A CONSTRUCTOR TAKING IN A JSON OBJECT to create comparisons
// 2. CONVERT JSON
//    - readonly arrays TO SETS
//    - convert keys/values to maps
interface SBOM {
  name: string;
}

interface SBOMConflict {
  conflictTypes?:  readonly any[];
  conflicts?: readonly any[];
}

interface ComponentConflict {
  componentA?: attributes | null;
  componentB?: attributes | null;
  conflictTypes?: readonly string[];
  conflicts?: readonly any[];
}

export interface attributes {
  uuid?: string | null;
  name?: string | null;
  publisher?: string | null;
  unpackaged?: boolean;
  cpes?: readonly attributes[] | readonly string[] | readonly [] | null;
  purls?: readonly attributes[] | attributes | readonly [];
  swids?: readonly string[] | readonly [];
  uniqueId?: string | null;
  uniqueIDType?: string | null;
  children?: readonly string[] | readonly [];
  version?: string | null;
  vulnerabilities?: string[] | [];
  licenses?: readonly string[] | readonly [] | null;
  conflicts?: any[] | [];
  componentName?: string | null;
  appearances?: readonly Number[] | readonly []
  componentVersion?: readonly Number[] | readonly [] | string;
  packageManager?: string | null;
}

interface DiffReport {
  sbomConflict?: SBOMConflict;
  componentConflicts?: readonly ComponentConflict[];
}

export interface ComponentVersion {
  componentName: string | null;
  componentVersion: string | null;
  cpes: {[key: string]: attributes} | {};
  purls: {[key: string]: attributes} | {};
  swids: {[key: string]: attributes} | {}
  appearances: readonly number[] | readonly []; // number meaning SBOM ID
}

interface UniqueIdOccurrence {
  appearances?: readonly number[];
  uniqueIdType?: string;
}

export interface Comparison {
  targetSbom?: SBOM;
  diffReports: readonly DiffReport[];
  comparisons: {[key: string]: readonly ComponentVersion[]};
}
