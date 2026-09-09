import AdminLearningLevelCreateForm from '@/widgets/admin/AdminLearningLevelCreateForm';
export default async function Page({params}:{params:Promise<{id:string}>}){const {id}=await params;return <AdminLearningLevelCreateForm id={id}/>}
