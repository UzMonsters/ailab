'use client';
import {useParams} from 'next/navigation';import ReactionEditor from '@/widgets/admin/reaction/ReactionEditor';export default function Page(){const {id}=useParams<{id:string}>();return <ReactionEditor id={id}/>}
