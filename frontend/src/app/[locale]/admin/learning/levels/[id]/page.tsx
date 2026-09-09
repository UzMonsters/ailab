import LearningLevelEditor from '@/widgets/admin/learning-level/LearningLevelEditor';
export default async function Page({params}:{params:Promise<{id:string}>}){const {id}=await params;return <LearningLevelEditor id={id}/>}
