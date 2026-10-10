import {readFile} from 'node:fs/promises';

/** Local fixtures select provider-independent sections from the sole schema source.
 * Hosted installation always applies the complete SQL file in one migration. */
export async function initialSchemaSection(section:string):Promise<string> {
  const sql=await readFile(new URL('../../../db/0001_initial_schema.sql',import.meta.url),'utf8');
  const start=`-- BEGIN ${section}\n`,end=`-- END ${section}`;
  const normalized=sql.replace(/\r\n/g,'\n');
  const offset=normalized.indexOf(start);
  const finish=normalized.indexOf(end,offset+start.length);
  if(offset<0 || finish<0) throw Error(`Unknown initial schema section: ${section}`);
  return normalized.slice(offset+start.length,finish);
}
