'use server'

import { SERVER_PATH } from '@lib/constants/server.constants'
import Method from '@lib/data/method.data'
import Routes from '@lib/data/routes.data'
import { revalidatePath } from 'next/cache'
import { redirect } from 'next/navigation'
const THIS_PATH = 'projects'

const CURRENT_PATH = `${SERVER_PATH}/${THIS_PATH}`

export async function createProject(__prevState: unknown, formData: FormData) {
  try {
    const project = Object.fromEntries(formData.entries())
    const projectDTO = {
      name: project.name,
      description: project.description,
      tag: project.tag,
      ownerId: project.ownerId,
      openAccess: project.openAccess === 'true',
    }

    await fetch(CURRENT_PATH, {
      method: Method.POST,
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${String(project.jwt)}`,
      },
      body: JSON.stringify(projectDTO),
    })
    // biome-ignore lint/suspicious/noExplicitAny: server fetch errors vary by runtime
  } catch (error: any) {
    return {
      errors: {
        createProject: error.message,
      },
    }
  }
  revalidatePath(Routes.Projects)
  redirect(Routes.Projects)
}

export async function updateProject(__prevState: unknown, formData: FormData) {
  const project = Object.fromEntries(formData.entries())

  try {
    const projectDTO = {
      name: project.name,
      description: project.description,
      tag: project.tag,
      ownerId: project.ownerId,
      openAccess: project.openAccess === 'true',
    }

    await fetch(`${CURRENT_PATH}/${String(project.projectId)}`, {
      method: Method.PATCH,
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${String(project.jwt)}`,
      },
      body: JSON.stringify(projectDTO),
    })
    // biome-ignore lint/suspicious/noExplicitAny: server fetch errors vary by runtime
  } catch (error: any) {
    return {
      errors: {
        updateProject: error.message,
      },
    }
  }
  revalidatePath(`${Routes.Projects}/${String(project.projectId)}`)
  redirect(`${Routes.Projects}/${String(project.projectId)}`)
}

export async function addUserToTeam(_prevState: unknown, formData: FormData) {
  const userDTO = Object.fromEntries(formData.entries())

  try {
    await fetch(`${CURRENT_PATH}/${String(userDTO.projectId)}/team/${String(userDTO.username)}`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${String(userDTO.jwt)}`,
      },
    })

    // biome-ignore lint/suspicious/noExplicitAny: server fetch errors vary by runtime
  } catch (error: any) {
    return {
      errors: {
        username: error.message,
      },
    }
  }
  revalidatePath(`${Routes.Projects}/${String(userDTO.projectId)}`)
}

export async function removeUserFromTeam(_prevState: unknown, formData: FormData) {
  const userDTO = Object.fromEntries(formData.entries())

  try {
    await fetch(`${CURRENT_PATH}/${String(userDTO.projectId)}/team/${String(userDTO.username)}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${String(userDTO.jwt)}`,
      },
    })

    // biome-ignore lint/suspicious/noExplicitAny: server fetch errors vary by runtime
  } catch (error: any) {
    return {
      errors: {
        username: error.message,
      },
    }
  }
  revalidatePath(`${Routes.Projects}/${String(userDTO.projectId)}`)
}
