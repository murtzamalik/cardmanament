/** Generic record for reference data entities (banks, branches, etc.) */
export type RefDataRecord = Record<string, unknown>;

export type RefDataFormFieldType = 'text' | 'checkbox' | 'dropdown';

export interface RefDataFormFieldConfig {
  name: string;
  label: string;
  type: RefDataFormFieldType;
  required?: boolean;
  /** If true, field is read-only when editing */
  disabledWhenEdit?: boolean;
  /** For type 'dropdown': API path to load options (e.g. '/api/products') */
  optionsApiPath?: string;
  /** For type 'dropdown': field on option object to use as value */
  optionValueField?: string;
  /** For type 'dropdown': field on option object to use as display label */
  optionLabelField?: string;
}

export interface RefDataConfig {
  /** API path e.g. '/api/banks' */
  apiPath: string;
  /** Primary key field name e.g. 'bankCode' */
  idField: string;
  /** Table columns */
  columns: { field: string; header: string }[];
  /** Form fields for create/edit dialog */
  formFields: RefDataFormFieldConfig[];
}
