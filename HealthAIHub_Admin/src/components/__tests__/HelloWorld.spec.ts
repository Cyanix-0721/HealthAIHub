import { describe, it, expect } from 'vitest'

import { mount } from '@vue/test-utils'
import HelloWorld from '../HelloWorld.vue'

describe('HelloWorld', () => {
  it('renders properly', () => {
    const wrapper = mount(HelloWorld, {
      props: { msg1: 'Hello', msg2: 'Vitest', msg3: 'HealthAIHub' },
    })
    expect(wrapper.text()).toContain('Hello')
    expect(wrapper.text()).toContain('Vitest')
    expect(wrapper.text()).toContain('HealthAIHub')
  })
})
