'use server'

import { SERVER_PATH } from '@lib/constants/server.constants'
import Method from '@lib/data/method.data'
import Routes from '@lib/data/routes.data'
import { revalidatePath } from 'next/cache'
import { redirect } from 'next/navigation'
const THIS_PATH = 'users'

const CURRENT_PATH = `${SERVER_PATH}/${THIS_PATH}`

export async function createUser(_prevState: unknown, formData: FormData) {
  try {
    const user = Object.fromEntries(formData.entries())
    const userDTO = {
      firstName: user.firstName,
      lastName: user.lastName,
      username: String(user.username).toLocaleLowerCase(),
      password: user.password,
      email: String(user.email).toLocaleLowerCase(),
      roles: [String(user.role).toUpperCase()],
    }

    await fetch(CURRENT_PATH, {
      method: Method.POST,
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${String(user.jwt)}`,
      },
      body: JSON.stringify(userDTO),
    })
    // biome-ignore lint/suspicious/noExplicitAny: server fetch errors vary by runtime
  } catch (error: any) {
    return {
      errors: {
        user: error.message,
      },
    }
  }
  revalidatePath(Routes.Users)
  redirect(Routes.Users)
}

export async function updateUser(_prevState: unknown, formData: FormData) {
  try {
    const user = Object.fromEntries(formData.entries())
    const userDTO = {
      firstName: user.firstName,
      lastName: user.lastName,
      username: user.username,
      email: user.email,
      roles: [String(user.role).toUpperCase()],
    }

    await fetch(`${CURRENT_PATH}/${String(user.id)}`, {
      method: Method.PATCH,
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${String(user.jwt)}`,
      },
      body: JSON.stringify(userDTO),
    })
    // biome-ignore lint/suspicious/noExplicitAny: server fetch errors vary by runtime
  } catch (error: any) {
    return {
      errors: {
        user: error.message,
      },
    }
  }
  revalidatePath(Routes.Users)
  redirect(Routes.Users)
}

export async function deleteUser(_prevState: unknown, formData: FormData) {
  try {
    const user = Object.fromEntries(formData.entries())

    const response = await fetch(`${CURRENT_PATH}/${user.id as string}`, {
      method: Method.DELETE,
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${String(user.jwt)}`,
      },
    })

    const result = await response.json()
    console.log('Delete User Response:', result)
    // biome-ignore lint/suspicious/noExplicitAny: server fetch errors vary by runtime
  } catch (error: any) {
    return {
      errors: {
        deleteUser: error.message,
      },
    }
  }
  revalidatePath(Routes.Users)
}
