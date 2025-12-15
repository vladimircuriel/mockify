import type { Project } from '@lib/entity/project.entity'
import { getAuthUser } from '@lib/utils/auth.utils'
import ProjectCard from '@components/cards/projectsCard/ProjectCard'
import ProjectModalClientWrapper from '@components/clientWrapper/ProjectModalClientWrapper'
import Header from '@components/header/Header'
import { getUserProjects } from '@lib/data/fetch/projects.fetch'
import { getTranslations } from 'next-intl/server'
import { cookies } from 'next/headers'

export default async function ProjectsPage() {
  let sortedProjects: Project[] = []
  const jwt = (await cookies()).get('session')?.value
  const [authPackage, authError] = await getAuthUser(jwt as string)
  if (authError) {
    return
  }

  const [projects, error] = await getUserProjects(jwt as string)
  if (error?.message === 'Project not found') {
    return
  }

  if (Array.isArray(projects) && projects.length > 0) {
    sortedProjects = [...projects].sort((a, b) => {
      if (a.id < b.id) {
        return -1
      }
      if (a.id > b.id) {
        return 1
      }
      return 0
    })
  }

  const t = await getTranslations('projectsPage')

  return (
    <main className="flex flex-col items-center w-full mt-6">
      <Header title={t('title')} description={t('description')}>
        <ProjectModalClientWrapper authPackage={authPackage} />
      </Header>
      <section className="w-full px-4 max-w-7xl lg:px-8">
        <div className="flex flex-col items-center justify-center w-full">
          {(projects?.length === 0 ||
            // @ts-ignore
            projects.message === 'No projects found') && (
            <p className="text-start text-default-400">{t('noProjects')}</p>
          )}
          {error && <p className="text-red-400 text-start">{t('error')}</p>}
        </div>
        <div className="grid grid-cols-1 gap-2 sm:grid-cols-2 md:grid-cols-3">
          {Array.isArray(sortedProjects) &&
            projects.length > 0 &&
            projects.map((project) => <ProjectCard project={project} key={project.id} />)}
        </div>
      </section>
    </main>
  )
}
