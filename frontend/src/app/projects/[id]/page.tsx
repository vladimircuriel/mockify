import BreadcrumbsBuilder from '@components/breadcrumbsBuilder/BreadcrumbsBuilder'
import Header from '@components/header/Header'
import ProjectSection from '@components/projects/ProjectSection'
import { getProject } from '@lib/data/fetch/projects.fetch'
import Routes from '@lib/data/routes.data'
import { getAuthUser } from '@lib/utils/auth.utils'
import { getTranslations } from 'next-intl/server'
import { cookies } from 'next/headers'
import { redirect } from 'next/navigation'

export default async function ProjectPage({ params }: { params: Promise<{ id: string }> }) {
  const id = (await params).id
  const jwt = (await cookies()).get('session')?.value
  const [authPackage, authError] = await getAuthUser(jwt as string)
  if (authError) {
    return
  }
  const [project, error] = await getProject({ token: jwt as string, id })
  if (error?.message === 'Project not found') {
    redirect(Routes.Projects)
  }

  const t = await getTranslations('projectPage')

  return (
    <main className="flex flex-col items-center w-full mt-6">
      <Header title={`${t('title')}: ${project.name}`} description={project.description}>
        <BreadcrumbsBuilder
          items={[
            { name: t('title'), href: Routes.Projects },
            { name: id, href: '#' },
          ]}
        />
      </Header>
      <div className="w-full px-4 max-w-7xl lg:px-8">
        <ProjectSection project={project} authPackage={authPackage} />
      </div>
      {error && <p className="text-red-400 text-start">{t('error')}</p>}
    </main>
  )
}
