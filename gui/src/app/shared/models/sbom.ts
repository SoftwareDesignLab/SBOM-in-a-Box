/** @Author Tina DiLorenzo */
/** @TODO Replace ? when finalized */

export interface SBOM {
  name: string;
  originFormat?: string;
  specVersion?: string;
  sbomVersion?: string;
  serialNumber?: string;
  supplier?: string;
  timestamp?: string;
  publisher?: string;

  // @TODO: implement fully later
  dependencyTree?: any;
  Signature?: any;
}
