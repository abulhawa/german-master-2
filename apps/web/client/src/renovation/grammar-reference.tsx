import { useEffect, useId, useRef, useState } from 'react';
import { BookOpen, ArrowLeft, X, ArrowRight } from 'lucide-react';
import reference from '../../../../../content/reference/grammar-reference.json';
import { FoundationButton } from '../foundation/controls';
import './grammar-reference.css';

const copy = {
  en: { open:'Grammar', title:'Grammar reference', search:'Search grammar', back:'All topics', close:'Close reference', rule:'Rule', examples:'Examples', mistakes:'Common mistakes', empty:'No matching topics.', language:'Grammar notes and examples are in German.' },
  de: { open:'Grammatik', title:'Grammatik-Referenz', search:'Grammatik suchen', back:'Alle Themen', close:'Referenz schließen', rule:'Regel', examples:'Beispiele', mistakes:'Typische Fehler', empty:'Keine passenden Themen.', language:'Grammatikhinweise und Beispiele sind auf Deutsch.' },
};
type Table = (typeof reference.topics)[number]['tables'][number];
const searchable = reference.topics.map(topic => ({topic,text:[topic.title.en,topic.title.de,...topic.tables.flatMap(t=>[t.title,t.note,...t.headers,...t.rows.flat(),...t.examples,t.rule,...t.mistakes,...t.extraSections.flatMap(s=>[s.title,...s.items]),...t.coverageItems])].join('\n').toLocaleLowerCase('de')}));

function ReferenceTable({table,locale}:{table:Table;locale:'en'|'de'}) {
  const c=copy[locale];
  return <section className="gm-reference-section" lang="de">
    <h3>{table.title}</h3><p className="gm-meta">{table.note}</p>
    {table.useListLayout || table.headers.length < 4 ? <div className="gm-reference-facts">{table.rows.map((row,i)=><dl key={i}>{row.map((cell,j)=><div key={j}><dt>{table.headers[j]}</dt><dd>{cell}</dd></div>)}</dl>)}</div>
      : <div className="gm-reference-table-scroll" role="region" aria-label={table.title} tabIndex={0}><table><caption className="gm-sr-only">{table.title}</caption><thead><tr>{table.headers.map(h=><th scope="col" key={h}>{h}</th>)}</tr></thead><tbody>{table.rows.map((row,i)=><tr key={i}>{row.map((cell,j)=>j===0?<th scope="row" key={j}>{cell}</th>:<td key={j}>{cell}</td>)}</tr>)}</tbody></table></div>}
    {[[c.rule,table.rule?[table.rule]:[]],[c.examples,table.examples],[c.mistakes,table.mistakes],...table.extraSections.map(s=>[s.title,s.items])] .map(([label,items],i)=> (items as string[]).length>0 && <details key={i} open={i===0}><summary lang={i<3?locale:'de'}>{label as string}</summary>{(items as string[]).map((item,j)=><p key={j}>{item}</p>)}</details>)}
  </section>;
}

/** Read-only overlay: opening a reference never advances, grades or replaces practice. */
export function GrammarReference({locale='en'}:{locale?:'en'|'de'}) {
  const c=copy[locale], dialog=useRef<HTMLDialogElement>(null);
  const headingId=useId();
  const [open,setOpen]=useState(false),[selected,setSelected]=useState<string|null>(null),[query,setQuery]=useState('');
  const topic=reference.topics.find(t=>t.id===selected);
  const results=searchable.filter(item=>item.text.includes(query.trim().toLocaleLowerCase('de'))).map(item=>item.topic);
  useEffect(()=>{
    if(!open || !dialog.current) return;
    const node=dialog.current, previous=document.body.style.overflow;
    document.body.style.overflow='hidden';
    if(!node.open) node.showModal();
    return ()=>{document.body.style.overflow=previous;if(node.open)node.close();};
  },[open]);
  return <>
    <FoundationButton className="gm-secondary gm-grammar-trigger" onClick={()=>{setSelected(null);setQuery('');setOpen(true);}}><BookOpen aria-hidden="true"/>{c.open}</FoundationButton>
    <dialog ref={dialog} className="gm-grammar-dialog" aria-labelledby={headingId} onClose={()=>setOpen(false)}>
      <header><h2 id={headingId}>{c.title}</h2><FoundationButton className="gm-secondary" autoFocus onClick={()=>dialog.current?.close()}><X aria-hidden="true"/>{c.close}</FoundationButton></header>
      <div className="gm-reference-body" key={topic?.id??'menu'}>
        {topic ? <><FoundationButton className="gm-secondary gm-reference-back" onClick={()=>setSelected(null)}><ArrowLeft aria-hidden="true"/>{c.back}</FoundationButton><h2>{topic.title[locale]}</h2>{topic.tables.map(table=><ReferenceTable key={table.title} table={table} locale={locale}/>)}</>
          : <><p className="gm-meta">{c.language}</p><label className="gm-field">{c.search}<input type="search" value={query} onChange={e=>setQuery(e.target.value)}/></label><div className="gm-reference-menu">{results.map(item=><FoundationButton key={item.id} className="gm-secondary" onClick={()=>setSelected(item.id)}><span>{item.title[locale]}</span><ArrowRight aria-hidden="true"/></FoundationButton>)}</div>{results.length===0&&<p role="status">{c.empty}</p>}</>}
      </div>
    </dialog>
  </>;
}
