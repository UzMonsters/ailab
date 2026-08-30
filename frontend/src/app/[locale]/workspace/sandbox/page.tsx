import { SandboxWorkspace } from '@/widgets/sandbox/SandboxWorkspace';
import { ErrorBoundary } from '@/shared/ui/ErrorBoundary';

export default function SandboxPage() {
  return (
    <ErrorBoundary>
      <SandboxWorkspace />
    </ErrorBoundary>
  );
}
