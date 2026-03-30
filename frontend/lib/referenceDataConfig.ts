import type { RefDataConfig } from '@/types/reference-data';

/** Reference data that use surrogate Long id in API paths (get/update/delete by id). */
const codeNameById = (code: string, name: string, apiPath: string): RefDataConfig => ({
  apiPath,
  idField: 'id',
  columns: [{ field: code, header: 'Code' }, { field: name, header: 'Name' }],
  formFields: [
    { name: code, label: 'Code', type: 'text', required: true, disabledWhenEdit: true },
    { name: name, label: 'Name', type: 'text' },
  ],
});

const configs: Record<string, RefDataConfig> = {
  branches: {
    apiPath: '/api/branches',
    idField: 'id',
    columns: [
      { field: 'branchCode', header: 'Branch Code' },
      { field: 'branchName', header: 'Branch Name' },
      { field: 'cityCode', header: 'City Code' },
      { field: 'countryCode', header: 'Country Code' },
      { field: 'swiftCode', header: 'SWIFT Code' },
    ],
    formFields: [
      { name: 'branchCode', label: 'Branch Code', type: 'text', required: true, disabledWhenEdit: true },
      { name: 'branchName', label: 'Branch Name', type: 'text' },
      { name: 'cityCode', label: 'City Code', type: 'text' },
      { name: 'countryCode', label: 'Country Code', type: 'text' },
      { name: 'swiftCode', label: 'SWIFT Code', type: 'text' },
    ],
  },
  accountStatuses: {
    apiPath: '/api/account-statuses',
    idField: 'id',
    columns: [
      { field: 'acctStatusCode', header: 'Code' },
      { field: 'acctStatusName', header: 'Name' },
      { field: 'description', header: 'Description' },
    ],
    formFields: [
      { name: 'acctStatusCode', label: 'Code', type: 'text', required: true, disabledWhenEdit: true },
      { name: 'acctStatusName', label: 'Name', type: 'text' },
      { name: 'description', label: 'Description', type: 'text' },
    ],
  },
  accountTypes: codeNameById('acctTypeCode', 'acctTypeName', '/api/account-types'),
  products: {
    apiPath: '/api/products',
    idField: 'id',
    columns: [
      { field: 'productCode', header: 'Product Code' },
      { field: 'productName', header: 'Product Name' },
      { field: 'isActive', header: 'Active' },
    ],
    formFields: [
      { name: 'productCode', label: 'Product Code', type: 'text', required: true, disabledWhenEdit: true },
      { name: 'productName', label: 'Product Name', type: 'text' },
      { name: 'isActive', label: 'Active', type: 'checkbox' },
    ],
  },
  cardTypes: {
    apiPath: '/api/card-types',
    idField: 'id',
    columns: [
      { field: 'cardTypeCode', header: 'Card Type Code' },
      { field: 'cardTypeName', header: 'Card Type Name' },
      { field: 'productCode', header: 'Product Code' },
      { field: 'isActive', header: 'Active' },
    ],
    formFields: [
      { name: 'cardTypeCode', label: 'Card Type Code', type: 'text', required: true, disabledWhenEdit: true },
      { name: 'cardTypeName', label: 'Card Type Name', type: 'text' },
      {
        name: 'productId',
        label: 'Product',
        type: 'dropdown',
        required: true,
        optionsApiPath: '/api/products',
        optionValueField: 'id',
        optionLabelField: 'productName',
      },
      { name: 'isActive', label: 'Active', type: 'checkbox' },
    ],
  },
  policies: {
    apiPath: '/api/policies',
    idField: 'id',
    columns: [
      { field: 'policyId', header: 'Policy ID' },
      { field: 'policyName', header: 'Name' },
      { field: 'policyDescription', header: 'Description' },
    ],
    formFields: [
      { name: 'policyId', label: 'Policy ID', type: 'text', required: true, disabledWhenEdit: true },
      { name: 'policyName', label: 'Name', type: 'text' },
      { name: 'policyDescription', label: 'Description', type: 'text' },
    ],
  },
  passwordExpressions: {
    apiPath: '/api/password-expressions',
    idField: 'id',
    columns: [
      { field: 'pwdExpId', header: 'ID' },
      { field: 'pwdExpName', header: 'Name' },
      { field: 'pwdExpression', header: 'Expression' },
    ],
    formFields: [
      { name: 'pwdExpId', label: 'ID', type: 'text', required: true, disabledWhenEdit: true },
      { name: 'pwdExpName', label: 'Name', type: 'text' },
      { name: 'pwdExpression', label: 'Expression', type: 'text' },
      { name: 'pwdExpDescription', label: 'Description', type: 'text' },
    ],
  },
  responseCodes: {
    apiPath: '/api/response-codes',
    idField: 'id',
    columns: [
      { field: 'code', header: 'Code' },
      { field: 'shortDescription', header: 'Short' },
      { field: 'fullDescription', header: 'Full' },
    ],
    formFields: [
      { name: 'code', label: 'Code', type: 'text', required: true, disabledWhenEdit: true },
      { name: 'shortDescription', label: 'Short Description', type: 'text' },
      { name: 'fullDescription', label: 'Full Description', type: 'text' },
    ],
  },
};

export type ReferenceDataKey = keyof typeof configs;

export function getReferenceDataConfig(key: ReferenceDataKey): RefDataConfig {
  const c = configs[key];
  if (!c) throw new Error(`Unknown reference data key: ${key}`);
  return c;
}
